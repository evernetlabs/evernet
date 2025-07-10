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

    def fetch(self):
        pass

    def get(self):
        pass

    def update(self):
        pass

    def delete(self):
        pass
