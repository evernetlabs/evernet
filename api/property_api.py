from flask import Flask, request

from service.property_service import PropertyService
from utils.api import required_param, optional_param
from utils.auth import authenticate_admin


class PropertyAPI:
    def __init__(self, app: Flask, property_service: PropertyService):
        self.app = app
        self.property_service = property_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/structure/properties")
        @authenticate_admin
        def create_property(admin, node_identifier):
            return self.property_service.create(
                node_identifier,
                request.args.get("structure_address"),
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("json_schema"),
                admin.get("identifier")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/properties")
        @authenticate_admin
        def fetch_properties(_, node_identifier):
            return self.property_service.fetch(
                node_identifier,
                request.args.get("structure_address")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/properties/<identifier>")
        @authenticate_admin
        def get_property(_, node_identifier, identifier):
            return self.property_service.get(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/structure/properties/<identifier>")
        @authenticate_admin
        def update_property(_, node_identifier, identifier):
            return self.property_service.update(
                node_identifier,
                request.args.get("structure_address"),
                identifier,
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/structure/properties/<identifier>")
        @authenticate_admin
        def delete_property(_, node_identifier, identifier):
            return self.property_service.delete(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )
