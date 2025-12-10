from flask import Flask
from service.structure_service import StructureService
from utils.api_utils import authenticate_admin, pagination_page, pagination_size, required_param

class StructureController:
    def __init__(self, app: Flask, structure_service: StructureService) -> None:
        self.app = app
        self.structure_service = structure_service

    def register(self):
        @self.app.post('/api/v1/admins/nodes/<node_identifier>/structures')
        @authenticate_admin()
        def create_structure(admin, node_identifier):
            return self.structure_service.create(
                node_identifier,
                required_param("identifier", str),
                required_param("display_name", str),
                required_param("description", str),
                admin.get("identifier")
            )

        @self.app.get('/api/v1/admins/nodes/<node_identifier>/structures')
        @authenticate_admin()
        def fetch_structures(_, node_identifier):
            return self.structure_service.fetch(
                node_identifier,
                pagination_page(),
                pagination_size()
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/structures")
        def public_fetch_structures(node_identifier):
            return self.structure_service.fetch(
                node_identifier,
                pagination_page(),
                pagination_size()
            )
