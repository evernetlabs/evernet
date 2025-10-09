import flask

from vertex.vertex_service import VertexService


class VertexAPI:
    def __init__(self, app: flask.Flask, vertex_service: VertexService):
        self.app = app
        self.vertex_service = vertex_service

    def register(self):

        @self.app.get('/api/v1/vertex/info')
        def vertex_info():
            return self.vertex_service.get_info()
