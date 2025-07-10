import datetime

from utils.ed25519 import *
from model.node import Node


class NodeService:
    def __init__(self):
        pass

    @staticmethod
    def create(identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if Node.select().where(Node.identifier == identifier).exists():
            raise Exception(f"Node {identifier} already exists")

        signing_private_key, signing_public_key = generate_ed25519_keys()

        node = Node(
            identifier=identifier,
            display_name=display_name,
            description=description,
            open=open,
            signing_private_key=private_key_to_string(signing_private_key),
            signing_public_key=public_key_to_string(signing_public_key),
            creator=creator
        )

        node.save()

        return {
            'id': node.get_id(),
            'identifier': identifier
        }

    @staticmethod
    def fetch(page=1, size=50) -> list[dict]:
        nodes = Node.select().paginate(page, size)
        result = []
        for node in nodes:
            result.append(NodeService.to_dict(node))
        return result

    @staticmethod
    def fetch_open(page=1, size=50) -> list[dict]:
        nodes = Node.select().where(Node.open == True).paginate(page, size)
        result = []
        for node in nodes:
            result.append(NodeService.to_dict(node))
        return result

    @staticmethod
    def get(identifier: str):
        node = Node.select().where(Node.identifier == identifier).get_or_none()

        if not node:
            raise Exception(f"Node {identifier} not found")

        return NodeService.to_dict(node)

    @staticmethod
    def get_signing_private_key(identifier: str):
        node = Node.select().where(Node.identifier == identifier).get()
        if not node:
            raise Exception(f"Node {identifier} not found")
        return string_to_private_key(node.signing_private_key)

    @staticmethod
    def update(identifier: str, display_name: str, description: str) -> dict:
        count = Node.update(
            identifier=identifier,
            display_name=display_name,
            description=description,
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(Node.identifier == identifier).execute()

        if count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
        }

    @staticmethod
    def update_open(identifier: str, open: bool) -> dict:
        count = Node.update(
            open=open,
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(Node.identifier == identifier).execute()

        if count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
        }

    @staticmethod
    def delete(identifier: str) -> dict:
        count = Node.delete().where(Node.identifier == identifier).execute()

        if count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    @staticmethod
    def reset_signing_keys(identifier: str) -> dict:
        signing_private_key, signing_public_key = generate_ed25519_keys()

        count = Node.update(
            signing_private_key=private_key_to_string(signing_private_key),
            signing_public_key=public_key_to_string(signing_public_key),
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(Node.identifier == identifier).execute()

        if count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
            "signing_public_key": public_key_to_string(signing_public_key),
        }

    @staticmethod
    def exists(identifier: str) -> bool:
        return Node.select().where(Node.identifier == identifier).exists()

    @staticmethod
    def to_dict(node: Node) -> dict:
        return {
            'id': node.get_id(),
            'identifier': node.identifier,
            'display_name': node.display_name,
            'description': node.description,
            'open': node.open,
            'signing_public_key': node.signing_public_key,
            'creator': node.creator,
            'created_at': node.created_at,
            'updated_at': node.updated_at,
        }
