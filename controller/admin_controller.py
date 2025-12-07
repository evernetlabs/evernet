from flask import Flask
from utils.api_utils import required_param

from service.admin_service import AdminService

class AdminController:
    def __init__(self, app: Flask, admin_service: AdminService) -> None:
        self.app = app
        self.admin_service = admin_service

    def register(self):

        @self.app.post('/api/v1/admins/init')
        def init_admin():
            return self.admin_service.init(
                required_param("identifier", str),
                required_param("password", str),
                required_param("vertex_endpoint", str),
                required_param("vertex_display_name", str),
                required_param("vertex_description", str)
            )
