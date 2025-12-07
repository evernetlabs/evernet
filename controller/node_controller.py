from flask import Flask
from service.node_service import NodeService
from utils.api_utils import required_param, authenticate_admin

class NodeController:
    def __init__(self, app: Flask, node_service: NodeService) -> None:
        self.app = app
        self.node_service = node_service

    def register(self):
        
        @self.app.post("/api/v1/admins/nodes")
        @authenticate_admin()
        def create_node(admin):
            return self.node_service.create(
                required_param("identifier"),
                required_param("display_name"),
                required_param("description"),
                admin["identifier"]
            )
