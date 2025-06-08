package name.modid.client.renderer;

import name.modid.PacifistRoute;
import name.modid.block.entity.MortarBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MortarHudRenderer {
    private static long lastGrindTime = 0;
    private static boolean lastGrindSuccess = false;
    private static float resultAnimationTime = 0.0f;
    private static final float RESULT_ANIMATION_DURATION = 140.0f;

    public static void register() {
        HudRenderCallback.EVENT.register(MortarHudRenderer::renderHud);
    }

    private static void renderHud(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.currentScreen != null) return;

        if (client.crosshairTarget instanceof BlockHitResult hitResult) {
            BlockPos pos = hitResult.getBlockPos();
            if (client.world.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
                checkGrindResult(mortar);

                renderProgressBar(context, mortar.getGrindProgress(), mortar, tickDelta);

                if (resultAnimationTime > 0.0f) {
                    renderGrindResult(context, lastGrindSuccess, tickDelta);
                    resultAnimationTime -= tickDelta;
                }
            }
        }
    }

    private static void checkGrindResult(MortarBlockEntity mortar) {
        if (mortar.getLastGrindTime() != lastGrindTime && mortar.getLastGrindTime() > 0) {
            lastGrindTime = mortar.getLastGrindTime();
            lastGrindSuccess = mortar.getLastGrindSuccess();
            resultAnimationTime = RESULT_ANIMATION_DURATION;
        }
    }

    private static void renderProgressBar(DrawContext context, float progress, MortarBlockEntity mortar, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();

        int barWidth = 200;
        int barHeight = 20;
        int x = (screenWidth - barWidth) / 2;
        int y = 30;

        // Определяем цвет в зависимости от материала
        int fillColor = getFillColor(mortar);
        int glowColor = getGlowColor(mortar);

        drawStyledBackground(context, x, y, barWidth, barHeight);

        // Рендерим заполнение прогресса
        if (progress > 0.0f) {
            renderProgressFill(context, x, y, barWidth, barHeight, progress, fillColor, glowColor, tickDelta);
            // Рендерим анимированные частицы на прогресс-баре
            renderProgressParticles(context, x, y, barWidth, barHeight, progress, glowColor, tickDelta);
        }

        // Текст с процентами
        renderProgressText(context, x, y, barWidth, barHeight, progress, mortar, fillColor);

        // Название материала
        renderMaterialName(context, x, y, barWidth, mortar, fillColor);
    }

    private static void renderGrindResult(DrawContext context, boolean success, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();

        int x = screenWidth / 2;
        int y = 75;


        float normalizedTime = resultAnimationTime / RESULT_ANIMATION_DURATION;
        float scale = 1.0f;
        float alpha = 1.0f;
        float rotation = 0.0f;

        if (normalizedTime > 0.9f) {

            float appearTime = (normalizedTime - 0.9f) / 0.1f;
            scale = MathHelper.lerp(1.0f - appearTime, 0.0f, 1.2f);
            alpha = 1.0f - appearTime;
        } else if (normalizedTime > 0.8f && normalizedTime <= 0.9f) {

            float stabilizeTime = (normalizedTime - 0.8f) / 0.1f;
            scale = MathHelper.lerp(1.0f - stabilizeTime, 1.2f, 1.0f);
            alpha = 1.0f;
        } else if (normalizedTime > 0.2f && normalizedTime <= 0.8f) {

            float swayTime = (normalizedTime - 0.2f) / 0.6f;
            scale = 1.0f;
            alpha = 1.0f;

            rotation = (float) Math.sin(swayTime * Math.PI * 2) * 8.0f; // 2 колебания, ±8 градусов
        } else if (normalizedTime > 0.1f && normalizedTime <= 0.2f) {

            float prepareTime = (normalizedTime - 0.1f) / 0.1f;
            scale = MathHelper.lerp(1.0f - prepareTime, 1.0f, 1.1f);
            alpha = 1.0f;
            rotation = (float) Math.sin(prepareTime * Math.PI) * 3.0f; // Затухающее покачивание
        } else {

            float fadeTime = normalizedTime / 0.1f;
            scale = MathHelper.lerp(1.0f - fadeTime, 1.1f, 0.0f);
            alpha = fadeTime;
            rotation = 0.0f;
        }


        renderPixelSymbol(context, success, x, y, scale, alpha, rotation);


        Text resultText = success ?
                Text.translatable("gui.pacifist_route.grind.success") :
                Text.translatable("gui.pacifist_route.grind.failure");

        int baseColor = success ? 0x00FF00 : 0xFF4444;
        int textColor = addAlpha(baseColor, alpha);
        context.drawCenteredTextWithShadow(client.textRenderer, resultText, x, y + 50, textColor);
    }


    private static void renderPixelSymbol(DrawContext context, boolean success, int x, int y,
                                          float scale, float alpha, float rotation) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);


        if (rotation != 0.0f) {
            context.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
        }


        int pixelSize = (int) (2 * scale); // Размер одного пикселя

        if (success) {
            // Рисуем зеленую галочку как на изображении
            drawCheckmarkPixels(context, pixelSize, alpha);
        } else {
            // Рисуем красный крестик как на изображении
            drawCrossPixels(context, pixelSize, alpha);
        }

        context.getMatrices().pop();
    }


    private static void drawCheckmarkPixels(DrawContext context, int pixelSize, float alpha) {

        int outline = addAlpha(0x000000, alpha);          // Черная обводка
        int darkGreen = addAlpha(0x1B4D1B, alpha);       // Очень темно-зеленый
        int mediumGreen = addAlpha(0x2E7D2E, alpha);     // Темно-зеленый
        int lightGreen = addAlpha(0x4CBB4C, alpha);      // Средне-зеленый
        int brightGreen = addAlpha(0x66FF66, alpha);     // Яркий зеленый

        // Пиксельная карта галочки (16x13 пикселей)
        int[][] checkmark = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 5, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 3, 1, 0},
                {0, 1, 1, 1, 0, 0, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0},
                {1, 2, 4, 5, 1, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0, 0},
                {1, 2, 3, 4, 3, 1, 2, 3, 4, 4, 3, 2, 1, 0, 0, 0},
                {0, 1, 2, 3, 4, 3, 3, 4, 4, 3, 2, 1, 0, 0, 0, 0},
                {0, 0, 1, 2, 3, 4, 4, 4, 3, 2, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 3, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 2, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        int[] colors = {0, outline, darkGreen, mediumGreen, lightGreen, brightGreen};

        for (int row = 0; row < checkmark.length; row++) {
            for (int col = 0; col < checkmark[row].length; col++) {
                int colorIndex = checkmark[row][col];
                if (colorIndex > 0) {
                    int pixelX = (col - 8) * pixelSize;
                    int pixelY = (row - 6) * pixelSize;
                    context.fill(pixelX, pixelY, pixelX + pixelSize, pixelY + pixelSize, colors[colorIndex]);
                }
            }
        }
    }


    private static void drawCrossPixels(DrawContext context, int pixelSize, float alpha) {

        int outline = addAlpha(0x000000, alpha);          // Черная обводка
        int darkRed = addAlpha(0x4D1B1B, alpha);         // Очень темно-красный
        int mediumRed = addAlpha(0x7D2E2E, alpha);       // Темно-красный
        int lightRed = addAlpha(0xBB4C4C, alpha);        // Средне-красный
        int brightRed = addAlpha(0xFF6666, alpha);       // Яркий красный

        // Пиксельная карта крестика (13x13 пикселей)
        int[][] cross = {
                {0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0},
                {1, 2, 2, 2, 1, 0, 0, 0, 1, 2, 2, 2, 1},
                {1, 2, 3, 3, 2, 1, 0, 1, 2, 3, 3, 2, 1},
                {1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1},
                {0, 1, 2, 3, 4, 3, 2, 3, 4, 3, 2, 1, 0},
                {0, 0, 1, 2, 3, 4, 3, 4, 3, 2, 1, 0, 0},
                {0, 0, 0, 1, 2, 3, 5, 3, 2, 1, 0, 0, 0},
                {0, 0, 1, 2, 3, 4, 3, 4, 3, 2, 1, 0, 0},
                {0, 1, 2, 3, 4, 3, 2, 3, 4, 3, 2, 1, 0},
                {1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1},
                {1, 2, 3, 3, 2, 1, 0, 1, 2, 3, 3, 2, 1},
                {1, 2, 2, 2, 1, 0, 0, 0, 1, 2, 2, 2, 1},
                {0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0}
        };

        int[] colors = {0, outline, darkRed, mediumRed, lightRed, brightRed};

        for (int row = 0; row < cross.length; row++) {
            for (int col = 0; col < cross[row].length; col++) {
                int colorIndex = cross[row][col];
                if (colorIndex > 0) {
                    int pixelX = (col - 6) * pixelSize;
                    int pixelY = (row - 6) * pixelSize;
                    context.fill(pixelX, pixelY, pixelX + pixelSize, pixelY + pixelSize, colors[colorIndex]);
                }
            }
        }
    }

    private static void drawStyledBackground(DrawContext context, int x, int y, int width, int height) {

        context.fill(x - 3, y - 3, x + width + 3, y + height + 3, 0x60000000);

        drawGradientRect(context, x - 1, y - 1, x + width + 1, y + height + 1, 0xFF2A2A2A, 0xFF1A1A1A);

        drawGradientRect(context, x, y, x + width, y + height, 0xFF404040, 0xFF202020);

        context.drawBorder(x - 1, y - 1, width + 2, height + 2, 0xFF8B8B8B);
        context.drawBorder(x, y, width, height, 0xFF606060);
    }

    private static void renderProgressFill(DrawContext context, int x, int y, int width, int height,
                                           float progress, int fillColor, int glowColor, float tickDelta) {
        int fillWidth = (int) (width * progress);
        if (fillWidth > 0) {
            // Основное заполнение с градиентом
            drawGradientRect(context, x, y, x + fillWidth, y + height,
                    brightenColor(fillColor, 0.2f), darkenColor(fillColor, 0.2f));

            // Эффект свечения
            if (fillWidth > 4) {
                drawGradientRect(context, x + 2, y + 2, x + fillWidth - 2, y + height - 2,
                        addAlpha(glowColor, 0.4f), addAlpha(glowColor, 0.1f));
            }

            // Анимированная полоска
            MinecraftClient client = MinecraftClient.getInstance();
            long time = client.world.getTime();
            float shimmer = (float) Math.sin(time * 0.1f) * 0.3f + 0.7f;

            if (fillWidth < width) {
                context.fill(x + fillWidth - 2, y, x + fillWidth, y + height,
                        addAlpha(brightenColor(fillColor, 0.5f), shimmer));
            }
        }
    }

    private static void renderProgressParticles(DrawContext context, int x, int y, int width, int height,
                                                float progress, int color, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        long time = client.world.getTime();

        for (int i = 0; i < 5; i++) {
            float cycle = (float) ((time + i * 17) % 60) / 60.0f;
            int particleX = x + (int) (width * progress * cycle);

            if (particleX >= x && particleX <= x + width * progress) {
                float intensity = (float) Math.sin(cycle * Math.PI);
                int particleY = y + height / 2 + (int) ((float) Math.sin((time + i * 11) * 0.08f) * 3);

                int particleColor = addAlpha(color, intensity * 0.8f);
                context.fill(particleX - 1, particleY - 1, particleX + 2, particleY + 2, particleColor);
            }
        }
    }

    private static void renderProgressText(DrawContext context, int x, int y, int width, int height,
                                           float progress, MortarBlockEntity mortar, int fillColor) {
        MinecraftClient client = MinecraftClient.getInstance();

        String text;
        if (progress > 0.0f) {
            text = String.format("%.0f%%", progress * 100);
        } else if (mortar.isEmpty()) {
            text = Text.translatable("gui.pacifist_route.mortar.empty").getString();
        } else {
            text = Text.translatable("gui.pacifist_route.mortar.ready").getString();
        }

        int textWidth = client.textRenderer.getWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;

        // Тень
        context.drawText(client.textRenderer, text, textX + 1, textY + 1, 0xFF000000, false);
        // Основной текст
        context.drawText(client.textRenderer, text, textX, textY, 0xFFFFFFFF, false);
    }

    private static void renderMaterialName(DrawContext context, int x, int y, int width,
                                           MortarBlockEntity mortar, int fillColor) {
        MinecraftClient client = MinecraftClient.getInstance();

        Text materialText = getMaterialText(mortar);
        if (materialText != null) {
            String materialName = materialText.getString();
            int materialWidth = client.textRenderer.getWidth(materialName);
            int materialX = x + (width - materialWidth) / 2;
            int materialY = y - 14;

            // Тень
            context.drawText(client.textRenderer, materialName, materialX + 1, materialY + 1, 0xFF000000, false);
            // Основной текст
            context.drawText(client.textRenderer, materialName, materialX, materialY, fillColor, false);
        }
    }

    private static Text getMaterialText(MortarBlockEntity mortar) {
        if (mortar.isEmpty()) {
            return Text.translatable("gui.pacifist_route.mortar.status.empty");
        }

        var firstItem = mortar.getFirstItemType();
        if (firstItem == null) return null;

        String itemName = firstItem.toString();
        if (itemName.contains("nitre")) {
            return Text.translatable("gui.pacifist_route.mortar.grinding.nitre");
        } else if (itemName.contains("sulfur")) {
            return Text.translatable("gui.pacifist_route.mortar.grinding.sulfur");
        }
        return Text.translatable("gui.pacifist_route.mortar.grinding.unknown");
    }

    private static int getFillColor(MortarBlockEntity mortar) {
        if (mortar.isEmpty()) {
            return 0xFF777777;
        }

        var firstItem = mortar.getFirstItemType();
        if (firstItem == null) return 0xFF888888;

        String itemName = firstItem.toString();
        if (itemName.contains("nitre")) {
            return 0xFF888888;
        } else if (itemName.contains("sulfur")) {
            return 0xFFFFD700;
        }
        return 0xFF888888;
    }

    private static int getGlowColor(MortarBlockEntity mortar) {
        if (mortar.isEmpty()) {
            return 0xFFAAAAAA;
        }

        var firstItem = mortar.getFirstItemType();
        if (firstItem == null) return 0xFFFFFFFF;

        String itemName = firstItem.toString();
        if (itemName.contains("nitre")) {
            return 0xFFFFFFFF;
        } else if (itemName.contains("sulfur")) {
            return 0xFFFFF8DC;
        }
        return 0xFFFFFFFF;
    }

    private static void drawGradientRect(DrawContext context, int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
        int height = y2 - y1;
        for (int i = 0; i < height; i++) {
            float ratio = (float) i / height;
            int color = interpolateColor(colorTop, colorBottom, ratio);
            context.fill(x1, y1 + i, x2, y1 + i + 1, color);
        }
    }

    private static int interpolateColor(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * (1 - factor));
        int g = (int) (((color >> 8) & 0xFF) * (1 - factor));
        int b = (int) ((color & 0xFF) * (1 - factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int brightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + factor)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int addAlpha(int color, float alpha) {
        int a = (int) (255 * MathHelper.clamp(alpha, 0.0f, 1.0f));
        return (a << 24) | (color & 0x00FFFFFF);
    }
}