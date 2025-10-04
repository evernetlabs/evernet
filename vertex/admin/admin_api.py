import flask

from utils.api_utils import required_param
from utils.auth_utils import authenticate_admin
from vertex.admin.admin_manager import AdminManager


class AdminApi:
    def __init__(self, app: flask.Flask, admin_manager: AdminManager):
        self.app = app
        self.admin_manager = admin_manager

    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin_api():
            return self.admin_manager.init(
                required_param("identifier"),
                required_param("password"),
                required_param("vertex_endpoint"),
                required_param("vertex_display_name"),
                required_param("vertex_description")
            )

        @self.app.post('/api/v1/admins/token')
        def get_admin_token_api():
            return self.admin_manager.get_token(
                required_param('identifier'),
                required_param('password')
            )

        @self.app.get('/api/v1/admins/current')
        @authenticate_admin
        def get_current_admin_api(admin):
            return self.admin_manager.get(admin["identifier"])
