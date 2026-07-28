CREATE TABLE tickets (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         title VARCHAR(255) NOT NULL,
                         description TEXT NOT NULL,
                         status VARCHAR(50) NOT NULL DEFAULT 'NEW',
                         priority VARCHAR(50) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP NOT NULL DEFAULT now(),

--declared foreign key columns as uuids
                         user_id uuid not null,
                         assigned_to uuid null,

                         Constraint fk_tickets_creators
                             foreign key (user_id)
                                 references users(id)
                                 on delete restrict, --use this to prevent deletion of tasks. Does it prevent deletions of the users as well? It does, thats he whole point. If someone tries to deete user with tasks referencing to them then it is an issue.
                         Constraint fk_tickets_assigned_to
                             foreign key (assigned_to)
                                 references users(id)
                                 on delete set null
);

CREATE index idx_foreign_id on tickets(user_id);
create index idx_status on tickets(status);