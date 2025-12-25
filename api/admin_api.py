from service.admin_service import AdminService
from flask import Flask
from libs.api_utils import authenticate_admin, required_param

class AdminAPI:
    def __init__(self, app: Flask, admin_service: AdminService) -> None:
        self.app = app
        self.admin_service = admin_service
    
    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin():
            return self.admin_service.init(
                required_param("identifier"),
                required_param("password"),
                required_param("vertex_endpoint"),
                required_param("vertex_display_name"),
                required_param("vertex_description")
            )

        @self.app.post("/api/v1/admins/token")
        def get_admin_token():
            return self.admin_service.get_token(
                required_param("identifier"),
                required_param("password")
            )

        @self.app.get("/api/v1/admins/current")
        @authenticate_admin()
        def get_current_admin(admin):
            return self.admin_service.get(
                admin.get("identifier")
            )
            
        @self.app.put("/api/v1/admins/current/password")
        @authenticate_admin()
        def change_current_user_password(admin):
            return self.admin_service.change_password(
                admin.get("identifier"),
                required_param("password")
            )
