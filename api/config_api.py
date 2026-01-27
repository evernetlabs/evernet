from flask import Flask

from service.config_service import ConfigService
from utils.api import required_param
from utils.auth import authenticate_admin
from utils.secret import generate_secret


class ConfigAPI:
    def __init__(self, app: Flask, config_service: ConfigService) -> None:
        self.app = app
        self.config_service = config_service

    def register(self):

        @self.app.put('/api/v1/admins/configs/jwt-signing-key')
        @authenticate_admin
        def reset_jwt_signing_key(_):
            self.config_service.set_jwt_signing_key(generate_secret(128))

            return {
                "success": True,
            }

        @self.app.put('/api/v1/admins/configs/federation-protocol')
        @authenticate_admin
        def set_federation_protocol(_):
            self.config_service.set_federation_protocol(required_param("federation_protocol"))

            return {
                "success": True,
            }
