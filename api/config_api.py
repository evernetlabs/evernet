from flask import Flask

from service.config_service import ConfigService
from util.api import required_param
from util.auth import authenticate_admin


class ConfigAPI:
    def __init__(self, app: Flask, config_service: ConfigService):
        self.app = app
        self.config_service = config_service

    def register(self):

        @self.app.get("/api/v1/configs/vertex")
        def get_vertex_config():
            return {
                "endpoint": self.config_service.get_vertex_endpoint(),
                "display_name": self.config_service.get_vertex_display_name(),
                "description": self.config_service.get_vertex_description(),
            }

        @self.app.get("/api/v1/configs/federation-protocol")
        @authenticate_admin
        def get_federation_protocol(admin):
            return {
                "federation_protocol": self.config_service.get_federation_protocol(),
            }

        @self.app.put("/api/v1/configs/federation-protocol")
        @authenticate_admin
        def set_federation_protocol(admin):
            self.config_service.set_federation_protocol(
                required_param("federation_protocol")
            )
            return {
                "success": True,
            }

        @self.app.put("/api/v1/configs/jwt-signing-key")
        @authenticate_admin
        def reset_jwt_signing_key(admin):
            self.config_service.reset_jwt_signing_key()
            return {
                "success": True,
            }

        @self.app.put("/api/v1/configs/vertex/endpoint")
        @authenticate_admin
        def set_vertex_endpoint(admin):
            self.config_service.set_vertex_endpoint(
                required_param("endpoint")
            )
            return {
                "success": True,
            }

        @self.app.put("/api/v1/configs/vertex/display-name")
        @authenticate_admin
        def set_vertex_display_name(admin):
            self.config_service.set_vertex_display_name(
                required_param("display_name")
            )

            return {
                "success": True,
            }

        @self.app.put("/api/v1/configs/vertex/description")
        @authenticate_admin
        def set_vertex_description(admin):
            self.config_service.set_vertex_description(
                required_param("description")
            )

            return {
                "success": True,
            }
