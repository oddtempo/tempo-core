
-- Tạo bảng event_publication trong schema infra
CREATE TABLE IF NOT EXISTS event_publication (
  id UUID NOT NULL,
  completion_date TIMESTAMP WITH TIME ZONE,
  event_type TEXT NOT NULL,
  listener_id TEXT NOT NULL,
  publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
  serialized_event TEXT NOT NULL,
  PRIMARY KEY (id)
);
