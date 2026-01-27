from flask import Flask, request

from service.function_service import FunctionService
from utils.api import required_param, optional_param
from utils.auth import authenticate_admin


class FunctionAPI:
    def __init__(self, app: Flask, function_service: FunctionService):
        self.app = app
        self.function_service = function_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/structure/functions")
        @authenticate_admin
        def create_function(admin, node_identifier):
            return self.function_service.create(
                node_identifier,
                request.args.get("structure_address"),
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("input_json_schema"),
                required_param("output_json_schema"),
                admin.get("identifier")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/functions")
        @authenticate_admin
        def fetch_functions(_, node_identifier):
            return self.function_service.fetch(
                node_identifier,
                request.args.get("structure_address")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/functions/<identifier>")
        @authenticate_admin
        def get_function(_, node_identifier, identifier):
            return self.function_service.get(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/structure/functions/<identifier>")
        @authenticate_admin
        def update_function(_, node_identifier, identifier):
            return self.function_service.update(
                node_identifier,
                request.args.get("structure_address"),
                identifier,
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/structure/functions/<identifier>")
        @authenticate_admin
        def delete_function(_, node_identifier, identifier):
            return self.function_service.delete(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )
