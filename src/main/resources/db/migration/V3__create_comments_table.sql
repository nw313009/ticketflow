CREATE TABLE comments (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    content TEXT NOT NULL,
                    internal boolean NOT NULL default true,
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),


                --foreign key
                ticket_id uuid not null,
                user_id uuid not null,

               Constraint fk_tickets_comments foreign key (ticket_id) references tickets(id) on delete restrict,
               Constraint fk_users_comments foreign key (user_id) references users(id) on delete restrict
);

create index idx_comments_ticket_id on comments(ticket_id);--index what will be queried the most.
create index idx_comments_user_id on comments(user_id);

