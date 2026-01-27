import requests

from exception.types import ClientException, NotFoundException
from service.config_service import ConfigService
from service.structure_service import StructureService


class StructureCloneService:

    def __init__(self, structure_service: StructureService, config_service: ConfigService):
        self.structure_service = structure_service
        self.config_service = config_service

    def get_or_clone(self, structure_address: str, node_identifier: str) -> dict:
        structure_address_components = structure_address.split("/")
        if len(structure_address_components) != 4 or structure_address_components[2] != "structures":
            raise ClientException(f"Invalid structure address format for {structure_address}")

        structure_vertex_endpoint = structure_address_components[0]
        structure_node_identifier  = structure_address_components[1]

        structure = self.structure_service.get_without_exception(node_identifier, structure_address)

        if structure is not None:
            return structure

        vertex_endpoint = self.config_service.get_vertex_endpoint()

        if vertex_endpoint == structure_vertex_endpoint:
            if structure_node_identifier == node_identifier:
                raise NotFoundException(f"Structure {structure_address} not found on node {node_identifier}")
            else:
                structure_from_another_node = self.structure_service.get(structure_node_identifier, structure_address)

                self.structure_service.cache(
                    node_identifier,
                    structure_from_another_node.get("address"),
                    structure_from_another_node.get("display_name"),
                    structure_from_another_node.get("description"),
                    "system_cloner"
                )

                return structure_from_another_node
        else:
            resp = requests.get(f"{self.config_service.get_federation_protocol()}://{structure_vertex_endpoint}/api/v1/nodes/{structure_node_identifier}/structure", params={
                "address": structure_address,
            })

            resp.raise_for_status()
            structure_from_another_vertex = resp.json()

            self.structure_service.cache(
                node_identifier,
                structure_from_another_vertex.get("address"),
                structure_from_another_vertex.get("display_name"),
                structure_from_another_vertex.get("description"),
                "system_cloner"
            )

            return structure_from_another_vertex

        raise NotFoundException(f"Structure {structure_address} not found on node {node_identifier}")
