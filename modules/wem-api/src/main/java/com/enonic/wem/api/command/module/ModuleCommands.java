package com.enonic.wem.api.command.module;


public final class ModuleCommands
{
    public CreateModule create()
    {
        return new CreateModule();
    }

    public UpdateModule update()
    {
        return new UpdateModule();
    }

    public DeleteModule delete()
    {
        return new DeleteModule();
    }

    public GetModules list()
    {
        return new GetModules();
    }

    public GetModule get()
    {
        return new GetModule();
    }

    public GetModules getAll()
    {
        return new GetModules().all();
    }

    public GetModuleResource getResource()
    {
        return new GetModuleResource();
    }

    public CreateModuleResource createResource()
    {
        return new CreateModuleResource();
    }
}
