from flask import Flask
from service.config_service import ConfigService
from utils.api_utils import required_param, authenticate_admin
from utils.secret_utils import generate_secret

class ConfigController:
    def __init__(self, app: Flask, config_service: ConfigService) -> None:
        self.app = app
        self.config_service = config_service

    def register(self):
        
        @self.app.put('/api/v1/admins/configs/jwt-signing-key')
        @authenticate_admin()
        def update_config_jwt_signing_key(_):
            jwt_signing_key = generate_secret(128)
            self.config_service.set_jwt_signing_key(jwt_signing_key)
            return {
                "jwt_signing_key": jwt_signing_key
            }

        @self.app.put('/api/v1/admins/configs/federation-protocol')
        @authenticate_admin()
        def update_config_federation_protocol(_):
            self.config_service.set_federation_protocol(required_param("federation_protocol"))
            return {
                "success": True
            }

        @self.app.get('/api/v1/admins/configs/federation-protocol')
        @authenticate_admin()
        def get_config_federation_protocol(_):
            return {
                "federation_protocol": self.config_service.get_federation_protocol()
            }

        @self.app.put('/api/v1/admins/configs/vertex-endpoint')
        @authenticate_admin()
        def update_config_vertex_endpoint(_):
            self.config_service.set_vertex_endpoint(required_param("vertex_endpoint"))
            return {
                "success": True
            }

        @self.app.put('/api/v1/admins/configs/vertex-display-name')
        @authenticate_admin()
        def update_config_vertex_display_name(_):
            self.config_service.set_vertex_display_name(required_param("vertex_display_name"))
            return {
                "success": True
            }

        @self.app.put('/api/v1/admins/configs/vertex-description')
        @authenticate_admin()
        def update_config_vertex_description(_):
            self.config_service.set_vertex_description(required_param("vertex_description"))
            return {
                "success": True
            }


        @self.app.get('/api/v1/vertex')
        def get_config_vertex_info():
            return {
                "vertex_display_name": self.config_service.get_vertex_display_name(),
                "vertex_description": self.config_service.get_vertex_description(),
                "vertex_endpoint": self.config_service.get_vertex_endpoint()
            }
