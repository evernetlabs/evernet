from flask import Flask

from service.vertex_service import VertexService


class VertexAPI:
    def __init__(self, app: Flask, vertex_service: VertexService) -> None:
        self.app = app
        self.vertex_service = vertex_service

    def register(self):


        @self.app.get("/api/v1/vertex")
        def get_vertex_info():
            return self.vertex_service.get()
