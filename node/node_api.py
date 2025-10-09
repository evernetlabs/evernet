import flask

from node.node_service import NodeService
from utils.api_utils import authenticate_admin, required_param, optional_param, pagination_page, pagination_size


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

        @self.app.get('/api/v1/admins/nodes')
        @authenticate_admin
        def fetch_nodes(_):
            return self.node_service.fetch(pagination_page(), pagination_size())

        @self.app.get('/api/v1/nodes')
        def fetch_open_nodes():
            return self.node_service.fetch_open(pagination_page(), pagination_size())
