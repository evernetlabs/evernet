import flask

from node.node_service import NodeService
from utils.api_utils import authenticate_admin, required_param, optional_param


class NodeAPI:
    def __init__(self, app: flask.Flask, node_service: NodeService):
        self.app = app
        self.node_service = node_service

    def register(self):

        @self.app.post('/api/v1/admins/nodes')
        @authenticate_admin
        def create_node(admin):
            return self.node_service.create(
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("open", bool),
                admin["identifier"]
            )
