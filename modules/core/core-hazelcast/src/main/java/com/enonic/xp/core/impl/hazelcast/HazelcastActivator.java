package com.enonic.xp.core.impl.hazelcast;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Component(immediate = true, configurationPid = "com.enonic.xp.hazelcast")
public class HazelcastActivator
{
    private HazelcastInstance hazelcastInstance;

    private ServiceRegistration<HazelcastInstance> hazelcastInstanceReg;

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        Config config = new Config();
        config.setClassLoader( new ContainerSweepClassLoader( context.getBundle() ) );

        hazelcastInstance = Hazelcast.newHazelcastInstance( config );
        this.hazelcastInstanceReg = context.registerService( HazelcastInstance.class, hazelcastInstance, new Hashtable<>() );
    }

    @Deactivate
    public void deactivate()
    {
        hazelcastInstanceReg.unregister();
        hazelcastInstance.shutdown();
    }
}
