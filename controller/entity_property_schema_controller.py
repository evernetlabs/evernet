from flask import Flask

from service.entity_property_schema_service import EntityPropertySchemaService
from utils.api import authenticate_admin, required_param, optional_param


class EntityPropertySchemaController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):
        @self.app.post(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties")
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

        @self.app.get(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties")
        @authenticate_admin
        def fetch_entity_property_schemas(admin, node_identifier, entity_schema_identifier, entity_schema_version):
            return EntityPropertySchemaService.fetch(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
            )

        @self.app.get(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties/<identifier>")
        @authenticate_admin
        def get_entity_property_schema(admin, node_identifier, entity_schema_identifier, entity_schema_version,
                                       identifier):
            return EntityPropertySchemaService.get(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
                identifier
            )

        @self.app.put(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties/<identifier>")
        @authenticate_admin
        def update_entity_property_schema(admin, node_identifier, entity_schema_identifier, entity_schema_version,
                                          identifier):
            return EntityPropertySchemaService.update(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
                identifier,
                required_param("display_name"),
                optional_param("description"),
            )

        @self.app.put(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties/<identifier>/json-schema")
        @authenticate_admin
        def update_entity_property_schema_json_schema(admin, node_identifier, entity_schema_identifier,
                                                      entity_schema_version, identifier):
            return EntityPropertySchemaService.update_json_schema(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
                identifier,
                optional_param("json_schema"),
            )

        @self.app.delete(
            "/api/v1/nodes/<node_identifier>/entity-schemas/<entity_schema_identifier>/<entity_schema_version>/properties/<identifier>")
        @authenticate_admin
        def delete_entity_property_schema(admin, node_identifier, entity_schema_identifier, entity_schema_version,
                                          identifier):
            return EntityPropertySchemaService.delete(
                node_identifier,
                entity_schema_identifier,
                entity_schema_version,
                identifier
            )
