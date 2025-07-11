from model.entity_property_schema import EntityPropertySchema


class EntityPropertySchemaService:

    def __init__(self):
        pass

    @staticmethod
    def create(node_identifier: str, entity_schema_identifier: str, entity_schema_version: str, identifier: str,
               display_name: str, description: str, json_schema: str, creator: str) -> dict:
        if EntityPropertySchema.select().where(
            EntityPropertySchema.node_identifier == node_identifier,
            EntityPropertySchema.entity_schema_identifier == entity_schema_identifier,
            EntityPropertySchema.entity_schema_version == entity_schema_version,
            EntityPropertySchema.identifier == identifier,
        ).exists():
            raise Exception(f"Property schema {identifier} already exists for entity schema {entity_schema_identifier} with version {entity_schema_version} on node {node_identifier}")

        entity_property_schema = EntityPropertySchema(
            node_identifier=node_identifier,
            entity_schema_identifier=entity_schema_identifier,
            entity_schema_version=entity_schema_version,
            identifier=identifier,
            display_name=display_name,
            description=description,
            json_schema=json_schema,
            creator=creator,
        )

        entity_property_schema.save()

        return {
            "id": entity_property_schema.get_id(),
            "identifier": entity_property_schema.identifier,
            "node_identifier": entity_property_schema.node_identifier,
            "entity_schema_identifier": entity_property_schema.entity_schema_identifier,
            "entity_schema_version": entity_property_schema.entity_schema_version,
        }

    @staticmethod
    def fetch(node_identifier: str, entity_schema_identifier: str, entity_schema_version: str) -> list[dict]:
        entity_property_schemas = EntityPropertySchema.select().where(
            EntityPropertySchema.node_identifier == node_identifier,
            EntityPropertySchema.entity_schema_identifier == entity_schema_identifier,
            EntityPropertySchema.entity_schema_version == entity_schema_version,
        )

        result = []

        for entity_property_schema in entity_property_schemas:
            result.append(EntityPropertySchemaService.to_dict(entity_property_schema))

        return result

    @staticmethod
    def get(node_identifier: str, entity_schema_identifier: str, entity_schema_version: str, identifier: str) -> dict:
        entity_property_schema = EntityPropertySchema.select().where(
            EntityPropertySchema.node_identifier == node_identifier,
            EntityPropertySchema.entity_schema_identifier == entity_schema_identifier,
            EntityPropertySchema.entity_schema_version == entity_schema_version,
            EntityPropertySchema.identifier == identifier,
        ).get_or_none()

        if not entity_property_schema:
            raise Exception(f"Property schema {identifier} for entity schema {entity_schema_identifier} with version {entity_schema_version} on node {node_identifier} not found")

        return EntityPropertySchemaService.to_dict(entity_property_schema)

    @staticmethod
    def update():
        pass

    @staticmethod
    def delete():
        pass

    @staticmethod
    def to_dict(entity_property_schema: EntityPropertySchema) -> dict:
        return {
            "id": entity_property_schema.get_id(),
            "identifier": entity_property_schema.identifier,
            "node_identifier": entity_property_schema.node_identifier,
            "entity_schema_identifier": entity_property_schema.entity_schema_identifier,
            "entity_schema_version": entity_property_schema.entity_schema_version,
            "display_name": entity_property_schema.display_name,
            "description": entity_property_schema.description,
            "json_schema": entity_property_schema.json_schema,
            "creator": entity_property_schema.creator,
        }