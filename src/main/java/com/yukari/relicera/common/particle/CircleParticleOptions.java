package com.yukari.relicera.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukari.relicera.registry.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public record CircleParticleOptions(Vec3 normal, float startDiameter, float endDiameter,
                                    int duration, int opaqueTicks) implements ParticleOptions {
    public static final Codec<CircleParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("normal").forGetter(CircleParticleOptions::normal),
            Codec.floatRange(0.01F, 128.0F).fieldOf("start_diameter").forGetter(CircleParticleOptions::startDiameter),
            Codec.floatRange(0.01F, 128.0F).fieldOf("end_diameter").forGetter(CircleParticleOptions::endDiameter),
            Codec.intRange(1, 1200).fieldOf("duration").forGetter(CircleParticleOptions::duration),
            Codec.intRange(0, 1200).fieldOf("opaque_ticks").forGetter(CircleParticleOptions::opaqueTicks)
    ).apply(instance, CircleParticleOptions::new));

    public static final Deserializer<CircleParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public CircleParticleOptions fromCommand(ParticleType<CircleParticleOptions> type, StringReader reader)
                throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            float start = reader.readFloat();
            reader.expect(' ');
            float end = reader.readFloat();
            reader.expect(' ');
            int duration = reader.readInt();
            reader.expect(' ');
            int opaqueTicks = reader.readInt();
            return new CircleParticleOptions(new Vec3(x, y, z), start, end, duration, opaqueTicks);
        }

        @Override
        public CircleParticleOptions fromNetwork(ParticleType<CircleParticleOptions> type, FriendlyByteBuf buffer) {
            return new CircleParticleOptions(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                    buffer.readFloat(), buffer.readFloat(), buffer.readVarInt(), buffer.readVarInt());
        }
    };

    public CircleParticleOptions {
        double length = normal.length();
        normal = Double.isFinite(length) && length > 1.0E-6D
                ? normal.scale(1.0D / length) : new Vec3(0.0D, 1.0D, 0.0D);
        startDiameter = Float.isFinite(startDiameter) ? Mth.clamp(startDiameter, 0.01F, 128.0F) : 1.0F;
        endDiameter = Float.isFinite(endDiameter) ? Mth.clamp(endDiameter, 0.01F, 128.0F) : 1.0F;
        duration = Mth.clamp(duration, 1, 1200);
        opaqueTicks = Mth.clamp(opaqueTicks, 0, duration - 1);
    }

    @Override
    public ParticleType<CircleParticleOptions> getType() {
        return ModParticleTypes.CIRCLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeDouble(normal.x);
        buffer.writeDouble(normal.y);
        buffer.writeDouble(normal.z);
        buffer.writeFloat(startDiameter);
        buffer.writeFloat(endDiameter);
        buffer.writeVarInt(duration);
        buffer.writeVarInt(opaqueTicks);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s %s %s %s %s %d %d", ModParticleTypes.CIRCLE.getId(),
                normal.x, normal.y, normal.z, startDiameter, endDiameter, duration, opaqueTicks);
    }
}
