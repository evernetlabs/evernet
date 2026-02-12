from flask import Flask

from exception.errors import ClientError, ServerError, AuthenticationError, AuthorizationError, NotFoundError


def register_flask_exception_handler(app: Flask):
    @app.errorhandler(ClientError)
    def handle_client_error(e): return {"success": False, "message": str(e)}, 400

    @app.errorhandler(ServerError)
    def handle_server_error(e): return {"success": False, "message": str(e)}, 500

    @app.errorhandler(AuthenticationError)
    def handle_authentication_error(e): return {"success": False, "message": str(e)}, 401

    @app.errorhandler(AuthorizationError)
    def handle_authorization_error(e): return {"success": False, "message": str(e)}, 403

    @app.errorhandler(NotFoundError)
    def handle_not_found_error(e): return {"success": False, "message": str(e)}, 404

    @app.errorhandler(404)
    def page_not_found(e): return {"success": False, "message": str(e)}, 404

    @app.errorhandler(Exception)
    def unknown_error(e): return {"success": False, "message": str(e)}, 500
