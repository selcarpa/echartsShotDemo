package one.tain.echartsshotdemo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ShotRequest {
    private Collection<String> options;
}
