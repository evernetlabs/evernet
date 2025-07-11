from flask import Flask

from service.entity_property_schema_service import EntityPropertySchemaService
from utils.api import authenticate_admin, required_param, optional_param


class EntityPropertySchemaController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.post("/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties")
        @authenticate_admin
        def create_entity_property_schema(admin, node_identifier, entity_schema_identifier, entity_schema_version):
            return EntityPropertySchemaService.create(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                optional_param("json_schema"),
                admin["identifier"]
            )
