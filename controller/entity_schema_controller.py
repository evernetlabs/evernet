from flask import Flask

from service.entity_schema_service import EntitySchemaService
from utils.api import authenticate_admin, required_param, optional_param, page, size


class EntitySchemaController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/entity-schemas")
        @authenticate_admin
        def create_entity_schema(admin, node_identifier):
            return EntitySchemaService.create(
                node_identifier,
                required_param("identifier"),
                required_param("version"),
                required_param("display_name"),
                optional_param("description"),
                admin["identifier"],
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/entity-schemas")
        @authenticate_admin
        def fetch_entity_schemas(admin, node_identifier):
            return EntitySchemaService.fetch(node_identifier, page(), size())

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/entity-schemas/<identifier>/<version>")
        @authenticate_admin
        def get_entity_schema(admin, node_identifier, identifier: str, version: str):
            return EntitySchemaService.get(node_identifier, identifier, version)

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/entity-schemas/<identifier>/<version>")
        @authenticate_admin
        def update_entity_schema(admin, node_identifier, identifier: str, version: str):
            return EntitySchemaService.update(
                node_identifier,
                identifier,
                version,
                required_param("display_name"),
                optional_param("description"),
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/entity-schemas/<identifier>")
        @authenticate_admin
        def delete_entity_schema(admin, node_identifier, identifier: str, version: str):
            return EntitySchemaService.delete(node_identifier, identifier, version)
