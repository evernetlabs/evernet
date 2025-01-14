from flask import Flask


class VertexInfo:
    def __init__(self, app: Flask, vertex_name: str, vertex_endpoint: str, vertex_description: str):
        self.app = app
        self.vertex_name = vertex_name
        self.vertex_endpoint = vertex_endpoint
        self.vertex_description = vertex_description

    def register(self):

        @self.app.get('/info')
        def info():
            return {
                "name": self.vertex_name,
                "endpoint": self.vertex_endpoint,
                "description": self.vertex_description,
            }