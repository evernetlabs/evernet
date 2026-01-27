from flask import Flask, request

from service.structure_service import StructureService
from utils.api import required_param, optional_param, pagination_page, pagination_size
from utils.auth import authenticate_admin


class StructureAPI:
    def __init__(self, app: Flask, structure_service: StructureService):
        self.app = app
        self.structure_service = structure_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/structures")
        @authenticate_admin
        def create_structure(admin, node_identifier):
            return self.structure_service.create(
                node_identifier,
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                admin.get("identifier")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structures")
        @authenticate_admin
        def fetch_structures(admin, node_identifier):
            return self.structure_service.fetch(node_identifier, pagination_page(), pagination_size())

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure")
        @authenticate_admin
        def get_structure(admin, node_identifier):
            return self.structure_service.get(node_identifier, request.args.get("address"))

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/structure")
        @authenticate_admin
        def update_structure(admin, node_identifier):
            return self.structure_service.update(
                node_identifier,
                request.args.get("address"),
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/structure")
        @authenticate_admin
        def delete_structure(admin, node_identifier):
            return self.structure_service.delete(node_identifier, request.args.get("address"))

        @self.app.get("/api/v1/nodes/<node_identifier>/structure")
        def public_api_get_structure(node_identifier):
            return self.structure_service.get(node_identifier, request.args.get("address"))
