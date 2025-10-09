import flask

from admin.admin_service import AdminService
from utils.api_utils import required_param, authenticate_admin, pagination_size, pagination_page


class AdminAPI:
    def __init__(self, app: flask.Flask, admin_service: AdminService):
        self.app = app
        self.admin_service = admin_service

    def register(self):

        @self.app.post('/api/v1/admins/init')
        def init_admin():
            return self.admin_service.init(
                required_param("identifier"),
                required_param("password"),
                required_param("vertex_endpoint"),
                required_param("vertex_display_name"),
                required_param("vertex_description")
            )

        @self.app.post('/api/v1/admins/token')
        def get_admin_token():
            return self.admin_service.get_token(
                required_param("identifier"),
                required_param("password"),
            )

        @self.app.get('/api/v1/admins/current')
        @authenticate_admin
        def get_current_admin(admin):
            return self.admin_service.get(admin['identifier'])

        @self.app.put('/api/v1/admins/current/password')
        @authenticate_admin
        def change_current_admin_password(admin):
            return self.admin_service.change_password(
                admin['identifier'],
                required_param('password'),
            )

        @self.app.post('/api/v1/admins')
        @authenticate_admin
        def add_admin(admin):
            return self.admin_service.add(
                required_param('identifier'),
                admin['identifier'],
            )

        @self.app.get('/api/v1/admins')
        @authenticate_admin
        def fetch_admins(_):
            return self.admin_service.fetch(pagination_page(), pagination_size())


        @self.app.get('/api/v1/admins/<identifier>')
        @authenticate_admin
        def get_admin(_, identifier):
            return self.admin_service.get(identifier)
