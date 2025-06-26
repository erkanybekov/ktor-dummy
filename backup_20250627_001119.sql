--
-- PostgreSQL database dump
--

-- Dumped from database version 15.13
-- Dumped by pg_dump version 15.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: todos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todos (
    id character varying(36) NOT NULL,
    title character varying(200) NOT NULL,
    description text,
    is_completed boolean DEFAULT false NOT NULL,
    user_id character varying(36) NOT NULL,
    created_at bigint NOT NULL,
    updated_at bigint NOT NULL
);


ALTER TABLE public.todos OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id character varying(36) NOT NULL,
    email character varying(254) NOT NULL,
    name character varying(100) NOT NULL,
    password_hash text NOT NULL,
    is_email_verified boolean DEFAULT false NOT NULL,
    created_at bigint NOT NULL,
    updated_at bigint NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: todos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.todos (id, title, description, is_completed, user_id, created_at, updated_at) FROM stdin;
d60b5fe2-b056-4dfe-92f3-58208fa2a299	test12	TestPass123	f	58309164-3b37-4cf3-becd-c8586b199f82	1750960836130	1750960836130
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, name, password_hash, is_email_verified, created_at, updated_at) FROM stdin;
\.


--
-- Name: todos todos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todos
    ADD CONSTRAINT todos_pkey PRIMARY KEY (id);


--
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_unique UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: todos_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX todos_user_id ON public.todos USING btree (user_id);


--
-- Name: todos_user_id_is_completed; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX todos_user_id_is_completed ON public.todos USING btree (user_id, is_completed);


--
-- PostgreSQL database dump complete
--

