from flask import Flask
from service.node_service import NodeService
from utils.api_utils import optional_param, pagination_page, pagination_size, required_param, authenticate_admin

class NodeController:
    def __init__(self, app: Flask, node_service: NodeService) -> None:
        self.app = app
        self.node_service = node_service

    def register(self):
        
        @self.app.post("/api/v1/admins/nodes")
        @authenticate_admin()
        def create_node(admin):
            return self.node_service.create(
                required_param("identifier", str),
                required_param("display_name", str),
                required_param("description", str),
                required_param("open", bool),
                admin["identifier"]
            )

        @self.app.get("/api/v1/admins/nodes")
        @authenticate_admin()
        def fetch_nodes(_):
            return self.node_service.fetch(
                pagination_page(),
                pagination_size()
            )

        @self.app.get("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin()
        def get_node(_, identifier: str):
            return self.node_service.get(identifier)

        @self.app.put("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin()
        def update_node(_, identifier: str):
            return self.node_service.update(
                identifier,
                optional_param("display_name", str),
                optional_param("description", str)
            )

        @self.app.put("/api/v1/admins/nodes/<identifier>/open")
        @authenticate_admin()
        def update_node_open(_, identifier: str):
            return self.node_service.update_open(
                identifier,
                required_param("open", bool)
            )

        @self.app.delete("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin()
        def delete_node(_, identifier: str):
            return self.node_service.delete(identifier)
