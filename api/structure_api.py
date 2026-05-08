from flask import Flask

from service.structure_service import StructureService
from util.api import required_param, optional_param, pagination_page, pagination_size
from util.auth import authenticate_admin


class StructureAPI:
    def __init__(self, app: Flask, structure_service: StructureService):
        self.app = app
        self.structure_service = structure_service

    def register(self):

        @self.app.post("/api/v1/nodes/<node_identifier>/structures")
        @authenticate_admin
        def create_structure(admin, node_identifier):
            return self.structure_service.register(
                node_identifier,
                required_param("identifier"),
                required_param("version"),
                required_param("display_name"),
                optional_param("description"),
                optional_param("properties", dict),
                optional_param("functions", dict),
                optional_param("events", dict),
                optional_param("events", dict),
                optional_param("relationships", dict),
                admin["username"]
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/structures")
        def fetch_structures(node_identifier):
            return self.structure_service.fetch(
                node_identifier,
                pagination_page(),
                pagination_size()
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/structures/<identifier>/versions")
        def fetch_structure_versions(node_identifier, identifier):
            return self.structure_service.fetch_versions(
                node_identifier,
                identifier,
                pagination_page(),
                pagination_size()
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/structures/<identifier>/<version>")
        def get_structure(node_identifier: str, identifier: str, version: str):
            return self.structure_service.get(
                node_identifier,
                identifier,
                version
            )
