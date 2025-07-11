import datetime

from model.entity_schema import EntitySchema
from service.node_service import NodeService


class EntitySchemaService:
    def __init__(self):
        pass

    @staticmethod
    def create(node_identifier: str, identifier: str, version: str, display_name: str, description: str,
               creator: str) -> dict:
        if not NodeService.exists(node_identifier):
            raise Exception(f"Node {node_identifier} not found")

        if EntitySchema.select().where(
                EntitySchema.node_identifier == node_identifier,
                EntitySchema.identifier == identifier,
                EntitySchema.version == version
        ).exists():
            raise Exception(f"Entity schema {identifier} with version {version} already exists on node {node_identifier}")

        entity_schema = EntitySchema(
            node_identifier=node_identifier,
            identifier=identifier,
            version=version,
            display_name=display_name,
            description=description,
            creator=creator,
        )

        entity_schema.save()

        return {
            'id': entity_schema.get_id(),
            "node_identifier": node_identifier,
            'identifier': identifier,
            'version': version,
        }

    @staticmethod
    def fetch(node_identifier: str, page: int = 1, size: int = 50) -> list[dict]:
        entity_schemas = EntitySchema.select().where(EntitySchema.node_identifier == node_identifier).paginate(page, size)

        results = []
        for entity_schema in entity_schemas:
            results.append(EntitySchemaService.to_dict(entity_schema))

        return results

    @staticmethod
    def get(node_identifier: str, identifier: str, version: str) -> dict:
        entity_schema = EntitySchema.select().where(
            EntitySchema.node_identifier == node_identifier,
            EntitySchema.identifier == identifier,
            EntitySchema.version == version
        ).get_or_none()

        if not entity_schema:
            raise Exception(f"Entity schema {identifier} with version {version} not found on node {node_identifier}")

        return EntitySchemaService.to_dict(entity_schema)


    @staticmethod
    def update(node_identifier: str, identifier: str, version: str, display_name: str, description: str) ->  dict:
        count = EntitySchema.update(
            display_name=display_name,
            description=description,
            updated_at=datetime.datetime.now(datetime.timezone.utc),
        ).where(
            EntitySchema.node_identifier == node_identifier,
            EntitySchema.identifier == identifier,
            EntitySchema.version == version,
        ).execute()

        if count == 0:
            raise Exception(f"Entity schema {identifier} with version {version} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "version": version,
            "node_identifier": node_identifier
        }

    @staticmethod
    def delete(node_identifier: str, identifier: str, version: str) -> dict:
        count = EntitySchema.delete().where(
            EntitySchema.node_identifier == node_identifier,
            EntitySchema.identifier == identifier,
            EntitySchema.version == version
        ).execute()

        if count == 0:
            raise Exception(f"Entity schema {identifier} with version {version} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "version": version,
            "node_identifier": node_identifier
        }

    @staticmethod
    def to_dict(entity_schema: EntitySchema):
        return {
            'id': entity_schema.get_id(),
            'node_identifier': entity_schema.node_identifier,
            'identifier': entity_schema.identifier,
            'version': entity_schema.version,
            'display_name': entity_schema.display_name,
            'description': entity_schema.description,
            'creator': entity_schema.creator,
            'created_at': entity_schema.created_at,
            'updated_at': entity_schema.updated_at,
        }