package emanondev.deepquests.config;

import com.google.common.collect.Maps;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;
import java.util.Map;

class YmlRepresenter extends Representer {

    public YmlRepresenter() {
        this.multiRepresenters.put(Navigator.class, new RepresentMap() {
            @Override
            public Node representData(Object object) {
                Navigator section = (Navigator) object;
                return super.representData(section.localMap);
            }
        });

        if (ConfigUtils.isBukkit()) {
            this.multiRepresenters.put(org.bukkit.configuration.serialization.ConfigurationSerializable.class, new RepresentMap() {
                @Override
                public Node representData(Object object) {
                    org.bukkit.configuration.serialization.ConfigurationSerializable serializable = (org.bukkit.configuration.serialization.ConfigurationSerializable) object;
                    Map<String, Object> values = Maps.newLinkedHashMap();
                    values.put("==", org.bukkit.configuration.serialization.ConfigurationSerialization.getAlias(serializable.getClass()));
                    values.putAll(serializable.serialize());
                    return super.representData(values);
                }
            });
        }
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentMap() {
            @Override
            public Node representData(Object object) {
                ConfigurationSerializable serializable = (ConfigurationSerializable) object;
                Map<String, Object> values = Maps.newLinkedHashMap();
                values.put("==", ConfigurationSerialization.getAlias(serializable.getClass()));
                values.putAll(serializable.serialize());
                return super.representData(values);
            }
        });
    }
}
