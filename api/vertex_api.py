from flask import Flask

from service.vertex_service import VertexService
from utils.api import required_param
from utils.auth import authenticate_admin


class VertexAPI:
    def __init__(self, app: Flask, vertex_service: VertexService):
        self.app = app
        self.vertex_service = vertex_service

    def register(self):

        @self.app.get("/api/v1/vertex")
        def get_vertex():
            return self.vertex_service.get_info()

        @self.app.put("/api/v1/admins/vertex")
        @authenticate_admin
        def update_vertex(_):
            return self.vertex_service.update_info(
                required_param("endpoint"),
                required_param("display_name"),
                required_param("description")
            )
