ALTER TABLE public.movie ADD COLUMN IF NOT EXISTS filePath text NULL DEFAULT ''::text;
ALTER TABLE public.movie ADD COLUMN IF NOT EXISTS description text NULL DEFAULT ''::text;