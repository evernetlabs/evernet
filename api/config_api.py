from flask import Flask

from service.config_service import ConfigService
from util.api import authenticate_admin, required_param
from util.secret import generate_secret


class ConfigAPI:
    def __init__(self, app: Flask, config_service: ConfigService):
        self.app = app
        self.config_service = config_service

    def register(self):

        @self.app.put('/api/v1/admins/configs/vertex/endpoint')
        @authenticate_admin
        def set_vertex_endpoint():
            return self.config_service.set_vertex_endpoint(
                required_param('endpoint')
            )

        @self.app.put('/api/v1/admins/configs/vertex/display-name')
        @authenticate_admin
        def set_vertex_display_name():
            return self.config_service.set_vertex_display_name(
                required_param('display_name')
            )

        @self.app.put('/api/v1/admins/configs/vertex/description')
        @authenticate_admin
        def set_vertex_description():
            return self.config_service.set_vertex_description(
                required_param('description')
            )

        @self.app.put('/api/v1/admins/configs/jwt-signing-key')
        @authenticate_admin
        def reset_jwt_signing_key():
            return self.config_service.set_jwt_signing_key(generate_secret(128))

        @self.app.put('/api/v1/admins/configs/federation-protocol')
        @authenticate_admin
        def set_federation_protocol():
            return self.config_service.set_federation_protocol(
                required_param('protocol')
            )
