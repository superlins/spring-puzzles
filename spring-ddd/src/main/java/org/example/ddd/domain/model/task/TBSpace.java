package org.example.ddd.domain.model.task;

import org.example.ddd.domain.shard.ValueObject;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

/**
 * @author renc
 */
public class TBSpace implements ValueObject<TBSpace> {

    /** 表命名规范正则 */
    private static final Pattern VALID_PATTERN = Pattern.compile("[a-zA-Z]+\\w*");

    /** 任务标注表名 */
    private String name;

    /** 任务标注数据同步总量 */
    private Long count;

    private TBSpace(String name) {
        this(name, -1L);
    }

    private TBSpace(String name, Long count) {
        Assert.hasText(name, "任务标注表名不能为空");
        Assert.isTrue(VALID_PATTERN.matcher(name).matches(), "任务标注表名不符合命名规范：" + name);
        Assert.notNull(count, "任务标注数据同步总量不能为空");
        this.name = name;
        this.count = count;
    }

    public static TBSpace newTBSpace() {
        return new TBSpace(nextTbName());
    }

    static String nextTbName() {
        return "T_RU_RECORD_" + System.currentTimeMillis();
    }

    public String name() {
        return name;
    }

    public Long count() {
        return count;
    }

    @Override
    public boolean sameValueAs(TBSpace other) {
        if (!name.equals(other.name)) return false;
        return count.equals(other.count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBSpace tbSpace = (TBSpace) o;

        return sameValueAs(tbSpace);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + count.hashCode();
        return result;
    }

    TBSpace() {
    }
}
