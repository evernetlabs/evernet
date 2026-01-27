from flask import Flask

from service.node_service import NodeService
from utils.api import required_param, optional_param, pagination_page, pagination_size
from utils.auth import authenticate_admin


class NodeAPI:
    def __init__(self, app: Flask,node_service: NodeService):
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
                admin.get("identifier")
            )

        @self.app.get('/api/v1/admins/nodes')
        @authenticate_admin
        def fetch_nodes(_):
            return self.node_service.fetch(
                pagination_page(),
                pagination_size()
            )

        @self.app.get('/api/v1/nodes')
        def fetch_open_nodes():
            return self.node_service.fetch_open(
                pagination_page(),
                pagination_size()
            )

        @self.app.get('/api/v1/nodes/<identifier>')
        def get_node(identifier):
            return self.node_service.get(identifier)

        @self.app.get('/api/v1/admins/nodes/<identifier>')
        @authenticate_admin
        def admin_api_get_node(_, identifier):
            return self.node_service.get(identifier)

        @self.app.put('/api/v1/admins/nodes/<identifier>')
        @authenticate_admin
        def update_node(_, identifier):
            return self.node_service.update(
                identifier,
                optional_param("display_name"),
                optional_param("description"),
            )

        @self.app.delete('/api/v1/admins/nodes/<identifier>')
        @authenticate_admin
        def delete_node(_, identifier):
            return self.node_service.delete(identifier)

        @self.app.put('/api/v1/admins/nodes/<identifier>/open')
        @authenticate_admin
        def update_node_open_flag(_, identifier):
            return self.node_service.update_open_flag(
                identifier,
                required_param("open", bool)
            )

        @self.app.put('/api/v1/admins/nodes/<identifier>/signing-keys')
        @authenticate_admin
        def reset_node_signing_keys(_, identifier):
            return self.node_service.reset_signing_keys(identifier)
