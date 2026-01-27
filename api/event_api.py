from flask import Flask, request

from service.event_service import EventService
from utils.api import required_param, optional_param
from utils.auth import authenticate_admin


class EventAPI:
    def __init__(self, app: Flask, event_service: EventService) -> None:
        self.app = app
        self.event_service = event_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/structure/events")
        @authenticate_admin
        def create_event(admin, node_identifier):
            return self.event_service.create(
                node_identifier,
                request.args.get("structure_address"),
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("json_schema"),
                admin.get("identifier")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/events")
        @authenticate_admin
        def fetch_events(_, node_identifier):
            return self.event_service.fetch(
                node_identifier,
                request.args.get("structure_address")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/events/<identifier>")
        @authenticate_admin
        def get_event(_, node_identifier, identifier):
            return self.event_service.get(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/structure/events/<identifier>")
        @authenticate_admin
        def update_event(_, node_identifier, identifier):
            return self.event_service.update(
                node_identifier,
                request.args.get("structure_address"),
                identifier,
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/structure/events/<identifier>")
        @authenticate_admin
        def delete_event(_, node_identifier, identifier):
            return self.event_service.delete(
                node_identifier,
                request.args.get("structure_address"),
                identifier
            )
