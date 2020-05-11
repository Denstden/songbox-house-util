package songbox.house.util;

import static java.lang.Boolean.valueOf;

public class MaskUtil {
    private MaskUtil() {
    }

    private static boolean hasMask(long flags, long mask) {
        return (flags & mask) == mask;
    }

    public static boolean hasMask(long flags, Mask mask) {
        return hasMask(flags, mask.getMask());
    }

    private static long setMask(long flags, long mask) {
        return flags | mask;
    }

    public static long setMask(long flags, Mask mask) {
        return setMask(flags, mask.getMask());
    }

    public static long setMasks(Mask... masks) {
        Long result = 0L;
        for (Mask mask : masks) {
            result = setMask(result, mask.getMask());
        }

        return result;
    }

    private static long removeMask(long flags, long mask) {
        return flags & ~mask;
    }

    public static long removeMask(long flags, Mask mask) {
        return removeMask(flags, mask.getMask());
    }

    public static long toggleMask(long flags, Mask mask, boolean value) {
        return value ? setMask(flags, mask) : removeMask(flags, mask);
    }

    public static long setOptionFlags(long flags, Mask mask, String value) {
        return setOptionFlags(flags, mask.getMask(), value);
    }

    public static long setOptionFlags(long flags, long mask, String value) {
        if (value == null) {
            return flags;
        } else if (valueOf(value)) {
            return flags | mask;
        }
        return flags & ~mask;
    }
}
