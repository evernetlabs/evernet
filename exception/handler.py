from flask import Flask, jsonify
import traceback

from exception.types import ClientException, NotFoundException, AuthenticationException, AuthorizationException, \
    ServerException


def register_exception_handler(app: Flask):

    @app.errorhandler(404)
    def handle_404_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 404

    @app.errorhandler(ClientException)
    def handle_client_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 400

    @app.errorhandler(NotFoundException)
    def handle_not_found_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 404

    @app.errorhandler(AuthenticationException)
    def handle_authentication_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 401

    @app.errorhandler(AuthorizationException)
    def handle_authorization_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 403

    @app.errorhandler(ServerException)
    def handle_server_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 500

    @app.errorhandler(Exception)
    def handle_all_errors(e):
        print(traceback.format_exc())
        return jsonify({
            "success": False,
            "message": str(e)
        }), 500
