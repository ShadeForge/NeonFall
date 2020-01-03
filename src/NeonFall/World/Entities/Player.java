package NeonFall.World.Entities;

import NeonFall.Manager.InputManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Physics.AABB;
import NeonFall.World.RoundData;
import org.joml.*;

import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Usage:
 * Author: lbald
 * Last Update: 13.01.2016
 */
public class Player extends TexturedEntity {

    public static final int ANIM_STAND = 0;
    public static final int ANIM_LEFT = 1;
    public static final int ANIM_RIGHT = 2;
    public static final int ANIM_UP = 3;
    public static final int ANIM_DOWN = 4;
    public static final int ANIM_GETUP = 5;

    public static final int SIDE_TOP = 0;
    public static final int SIDE_RIGHT = 1;
    public static final int SIDE_BOTTOM = 2;
    public static final int SIDE_LEFT = 3;

    private final static float ROTATION_SPEED = 10f;
    public final static float PLAYER_POSITION_Z = 2f;
    private final static float PLAYER_POSITION_OFFSET = 0.625f;

    private AABB boundingBox;
    private Vector2f animationOffset;
    private Vector2f positionOffset;
    private float rotationOffset;
    private float currentRotation;
    private int animationState;
    private Vector2f position;
    private RoundData roundData;
    private boolean freefall;
    private boolean goUp;
    private boolean goFlat;
    private int sidePosition;
    private int startAnimationState;
    private float timer = 0;

    public Player(RoundData roundData, Vector2f position) {
        super(ResourceManager.PLAYER_MODEL_FILE, ResourceManager.TEX_SHIP_FILE, new Vector4f(1, 0, 0, 1));
        this.freefall = false;
        this.goUp = false;
        this.goFlat = false;
        this.roundData = roundData;
        this.position = position;
        this.sidePosition = SIDE_TOP;
        this.animationState = ANIM_STAND;
        this.startAnimationState = ANIM_STAND;
        this.animationOffset = new Vector2f(0, 0);
        this.positionOffset = new Vector2f(0, PLAYER_POSITION_OFFSET);
        this.rotationOffset = 0;
        this.currentRotation = 0;
        this.material.getDiffuse().set(1, 0, 0);
        Matrix3f mat = new Matrix3f().scale(0.0625f, 0.0625f, 0.25f);
        this.boundingBox = new AABB(mat.transform(new Vector3f(-1, -1, -1)), mat.transform(new Vector3f(1, 1, 1)));
    }

    @Override
    public void update(float delta) {

        timer+=delta;
        if(timer > 1) {
            timer %= 1;
            System.out.println("Position: " + position.x + ":" + position.y);
            System.out.println("Side: " + sidePosition);
            System.out.println("Freefall: " + (freefall ? "On" : "Off"));
        }

        if(freefall) {
            float extraRot = 0;

            if(animationState == ANIM_STAND || animationState == ANIM_GETUP) {
                Vector3f selector = null;

                if (InputManager.isKeyDown(GLFW_KEY_W)) {
                    selector = new Vector3f(0, 1, 0);
                    animationState = ANIM_UP;
                } else if (InputManager.isKeyDown(GLFW_KEY_S)) {
                    animationState = ANIM_DOWN;
                    selector = new Vector3f(0, -1, 0);
                } else if (InputManager.isKeyDown(GLFW_KEY_D)) {
                    animationState = ANIM_RIGHT;
                    selector = new Vector3f(-1, 0, 0);
                } else if (InputManager.isKeyDown(GLFW_KEY_A)) {
                    animationState = ANIM_LEFT;
                    selector = new Vector3f(1, 0, 0);
                }

                if (selector != null) {
                    Matrix3f rotMat = new Matrix3f().rotateZ(rotationOffset);
                    rotMat.transform(selector);
                    selector.add(position.x, position.y, 0);

                    goUp = roundData.isBlocked((int) selector.x - sideOffset(sidePosition), (int) selector.y + sideOffset(sidePosition + 1));
                }
            } else {
                if (animationState % 2 == 0)
                    currentRotation += ROTATION_SPEED * delta;
                else
                    currentRotation -= ROTATION_SPEED * delta;

                if (currentRotation < -(float) Math.PI || currentRotation > (float) Math.PI)
                    updatePlayerStates();

                if (goUp) {
                    switch (animationState) {
                        case ANIM_UP:
                            animationOffset.y = flatMove(currentRotation) * 0.5f * sideOffset(sidePosition);
                            animationOffset.x = flatMove(currentRotation) * 0.5f * sideOffset(sidePosition + 1);
                            roundData.camera.setLookUpRotationXZ(currentRotation + rotationOffset);
                            extraRot = currentRotation + rotationOffset;
                            break;
                        case ANIM_RIGHT:
                            animationOffset.x = -curveMove(currentRotation);
                            animationOffset.y = curveMove(currentRotation);
                            roundData.camera.setLookUpRotationXZ(-currentRotation / 2 + rotationOffset);
                            extraRot = -currentRotation / 2 + rotationOffset;
                            break;
                        case ANIM_DOWN:
                            animationOffset.x = -(float) (1 - Math.sin(currentRotation / 2)) * 0.25f * sideOffset(sidePosition);
                            animationOffset.y = (float) (1 - Math.sin(currentRotation / 2)) * 0.25f * sideOffset(sidePosition + 1);
                            break;
                        case ANIM_LEFT:
                            animationOffset.x = -curveMove(currentRotation) * sideOffset(sidePosition + 1);
                            animationOffset.y = curveMove(currentRotation) * sideOffset(sidePosition + 1);
                            roundData.camera.setLookUpRotationXZ(-currentRotation / 2 + rotationOffset);
                            extraRot = -currentRotation / 2 + rotationOffset;
                            break;
                    }
                } else {
                    switch (animationState) {
                        case ANIM_LEFT:
                            animationOffset.x = -flatMove(currentRotation) * sideOffset(sidePosition + 1);
                            animationOffset.y = -flatMove(currentRotation) * sideOffset(sidePosition);
                            break;
                        case ANIM_RIGHT:
                            animationOffset.x = -flatMove(currentRotation) * sideOffset(sidePosition + 1);
                            animationOffset.y = -flatMove(currentRotation) * sideOffset(sidePosition);
                            break;
                        case ANIM_UP:
                            animationOffset.x = flatMove(currentRotation) * sideOffset(sidePosition);
                            animationOffset.y = -flatMove(currentRotation) * sideOffset(sidePosition + 1);
                            break;
                        case ANIM_DOWN:
                            animationOffset.x = flatMove(currentRotation) * sideOffset(sidePosition);
                            animationOffset.y = -flatMove(currentRotation) * sideOffset(sidePosition + 1);
                            break;
                    }
                    animationOffset.x -= (float) Math.sin(rotationOffset) * 0.25f;
                    animationOffset.y += (float) Math.cos(rotationOffset) * 0.25f;
                }
            }
            if(animationState == ANIM_GETUP) {
                animationOffset.x = (float) Math.sin(currentRotation / 2) * 0.25f * sideOffset(sidePosition);
                animationOffset.y = -(float) Math.sin(currentRotation / 2) * 0.25f * sideOffset(sidePosition + 1);
            }
            modelMatrix = new Matrix4f().translate(position.x + animationOffset.x + positionOffset.x,
                    position.y + animationOffset.y + positionOffset.y,
                    PLAYER_POSITION_Z).rotateZ(rotationOffset + extraRot)
                    .scale(0.5f, 0.5f, 0.5f);
            roundData.camera.setCamPos(position.x + animationOffset.x + positionOffset.x - (float)Math.sin(rotationOffset) * 0.5f,
                    position.y + animationOffset.y + positionOffset.y + (float)Math.cos(rotationOffset) * 0.5f, 0);

        } else {

            if(InputManager.isKeyDown(GLFW_KEY_W)) {
                freefall = true;
                animationState = ANIM_GETUP;
            } else if (InputManager.isKeyDown(GLFW_KEY_A) || InputManager.isKeyDown(GLFW_KEY_D)) {
                if(InputManager.isKeyDown(GLFW_KEY_A)) {
                    animationState = ANIM_LEFT;
                } else {
                    animationState = ANIM_RIGHT;
                }
                if(startAnimationState == ANIM_STAND) {
                    startAnimationState = animationState;

                    int side = sidePosition;
                    if(animationState == ANIM_RIGHT) {
                        side++;
                        if(side > 3)
                            side = 0;
                    }
                    switch (side) {
                        case SIDE_TOP:
                            goUp = roundData.isBlocked((int) position.x + 1, (int) position.y + 1);
                            break;
                        case SIDE_RIGHT:
                            goUp = roundData.isBlocked((int) position.x - 1, (int) position.y + 1);
                            break;
                        case SIDE_BOTTOM:
                            goUp = roundData.isBlocked((int) position.x - 1, (int) position.y - 1);
                            break;
                        case SIDE_LEFT:
                            goUp = roundData.isBlocked((int) position.x + 1, (int) position.y - 1);
                            break;
                    }
                    if(animationState == ANIM_RIGHT) {
                        side++;
                        side %= 4;
                    }
                    switch (side) {
                        case SIDE_TOP:
                            goFlat = roundData.isBlocked((int) position.x + 1, (int) position.y);
                            break;
                        case SIDE_RIGHT:
                            goFlat = roundData.isBlocked((int) position.x, (int) position.y + 1);
                            break;
                        case SIDE_BOTTOM:
                            goFlat = roundData.isBlocked((int) position.x - 1, (int) position.y);
                            break;
                        case SIDE_LEFT:
                            goFlat = roundData.isBlocked((int) position.x, (int) position.y - 1);
                            break;
                    }
                }
            }

            if(animationState == ANIM_LEFT) {
                currentRotation -= ROTATION_SPEED * delta;
            } else if (animationState == ANIM_RIGHT) {
                currentRotation += ROTATION_SPEED * delta;
            }

            if (startAnimationState == ANIM_LEFT) {
                if(animationState == ANIM_LEFT && currentRotation < -(float)Math.PI) {
                    updatePlayerStates();
                } else if(currentRotation > 0) {
                    stopAnimation();
                }
            } else if (startAnimationState == ANIM_RIGHT) {
                if(animationState == ANIM_RIGHT && currentRotation > (float)Math.PI) {
                    updatePlayerStates();
                } else if(currentRotation < 0) {
                    stopAnimation();
                }
            }

            if(goUp) {
                int side = sidePosition;
                if(animationState == ANIM_RIGHT) {
                    side--;
                    if(side < 0)
                        side = 3;
                }

                switch (side) {
                    case SIDE_TOP:
                        animationOffset.x = curveMove(currentRotation);
                        animationOffset.y = curveMove(currentRotation);
                        break;
                    case SIDE_RIGHT:
                        animationOffset.x = -curveMove(currentRotation);
                        animationOffset.y = curveMove(currentRotation);
                        break;
                    case SIDE_BOTTOM:
                        animationOffset.x = -curveMove(currentRotation);
                        animationOffset.y = -curveMove(currentRotation);
                        break;
                    case SIDE_LEFT:
                        animationOffset.x = curveMove(currentRotation);
                        animationOffset.y = -curveMove(currentRotation);
                        break;
                }
                float rot = rotationFunction(currentRotation) + rotationOffset;
                modelMatrix = new Matrix4f().translate(position.x + animationOffset.x + positionOffset.x,
                        position.y + animationOffset.y + positionOffset.y,
                        PLAYER_POSITION_Z).rotateZ(-rot)
                        .scale(0.5f, 0.5f, 0.5f);
                if(sidePosition % 2 == 0) {
                    roundData.camera.setCamPos(position.x + positionOffset.x + (float) Math.sin(rot) * 0.125f,
                            position.y + positionOffset.y + animationOffset.y + (float) Math.cos(rot) * 0.5f, 0);
                } else {
                    roundData.camera.setCamPos(position.x + positionOffset.x + animationOffset.x - (float) Math.sin(rot) * 0.5f,
                            position.y + positionOffset.y - (float) Math.cos(rot) * 0.125f, 0);
                }
                roundData.camera.setLookUpRotationXZ(-currentRotation / 2 + rotationOffset);
            } else if(goFlat) {
                switch (sidePosition) {
                    case SIDE_TOP:
                        animationOffset.x = -flatMove(currentRotation);
                        break;
                    case SIDE_RIGHT:
                        animationOffset.y = -flatMove(currentRotation);
                        break;
                    case SIDE_BOTTOM:
                        animationOffset.x = flatMove(currentRotation);
                        break;
                    case SIDE_LEFT:
                        animationOffset.y = flatMove(currentRotation);
                        break;
                }
                float rot = rotationOffset;
                modelMatrix = new Matrix4f().translate(position.x + animationOffset.x + positionOffset.x,
                        position.y + animationOffset.y + positionOffset.y,
                        PLAYER_POSITION_Z).rotateZ(rot)
                        .scale(0.5f, 0.5f, 0.5f);
                roundData.camera.setCamPos(position.x + animationOffset.x + positionOffset.x - (float)Math.sin(rot) * 0.5f,
                        position.y + animationOffset.y + positionOffset.y + (float)Math.cos(rot) * 0.5f, 0);
                roundData.camera.setLookUpRotationXZ(rotationOffset);
            } else {
                switch (sidePosition) {
                    case SIDE_TOP:
                        animationOffset.x = directMove(currentRotation);
                        animationOffset.y = delayedMove(currentRotation);
                        break;
                    case SIDE_RIGHT:
                        animationOffset.x = -delayedMove(currentRotation);
                        animationOffset.y = directMove(currentRotation);
                        break;
                    case SIDE_BOTTOM:
                        animationOffset.x = -directMove(currentRotation);
                        animationOffset.y = -delayedMove(currentRotation);
                        break;
                    case SIDE_LEFT:
                        animationOffset.x = delayedMove(currentRotation);
                        animationOffset.y = -directMove(currentRotation);
                        break;
                }
                float rot = rotationFunction(currentRotation) + rotationOffset;
                modelMatrix = new Matrix4f().translate(position.x + animationOffset.x + positionOffset.x,
                        position.y + animationOffset.y + positionOffset.y,
                        PLAYER_POSITION_Z).rotateZ(rot)
                        .scale(0.5f, 0.5f, 0.5f);
                roundData.camera.setCamPos(position.x + animationOffset.x + positionOffset.x - (float)Math.sin(rot) * 0.5f,
                        position.y + animationOffset.y + positionOffset.y + (float)Math.cos(rot) * 0.5f, 0);
                roundData.camera.setLookUpRotationXZ(currentRotation / 2 + rotationOffset);
            }

            if(!roundData.isBlocked((int)position.x, (int)position.y) && animationState == ANIM_STAND) {
                freefall = true;
                animationState = ANIM_GETUP;
            }
        }
        Vector3f bbPos = new Vector3f(position.x + positionOffset.x + animationOffset.x * 1.125f,
                position.y + positionOffset.y + animationOffset.y * 1.125f,
                PLAYER_POSITION_Z);
        boundingBox.setPosition(bbPos);

        for(int x = (int)bbPos.x - 1; x < bbPos.x + 1; x++) {
            for(int y = (int)bbPos.y; y - 1 < bbPos.y + 1; y++) {
                if(roundData.isInMap(x, y)) {
                    LinkedList<BarEntity> barEntities = roundData.barEntities[x][y];
                    if(barEntities.size() > 0 && barEntities.getFirst().isColliding(boundingBox)) {
                        roundData.loosed = true;
                    }
                }
            }
        }
    }

    public void updatePlayerStates() {

        if(freefall) {
            if(goUp) {
                switch (animationState) {
                    case ANIM_LEFT:
                        position.x += sideOffset(sidePosition + 1) - sideOffset(sidePosition);
                        position.y += sideOffset(sidePosition + 1) + sideOffset(sidePosition);
                        sidePosition++;
                        break;
                    case ANIM_RIGHT:
                        position.x -= sideOffset(sidePosition + 1) + sideOffset(sidePosition);
                        position.y -= sideOffset(sidePosition) - sideOffset(sidePosition + 1);
                        sidePosition--;
                        break;
                    case ANIM_UP:
                        position.x -= sideOffset(sidePosition) * 2;
                        position.y += sideOffset(sidePosition + 1) + sideOffset(sidePosition + 1);
                        sidePosition += 2;
                        break;
                }

                sidePosition %= 4;

                if (sidePosition < 0)
                    sidePosition = 3;

                freefall = false;
            } else {
                switch (animationState) {
                    case ANIM_LEFT:
                        position.x += (float) sideOffset(sidePosition + 1);
                        position.y += (float) sideOffset(sidePosition);
                        break;
                    case ANIM_RIGHT:
                        position.x -= (float) sideOffset(sidePosition + 1);
                        position.y -= (float) sideOffset(sidePosition);
                        break;
                    case ANIM_UP:
                        position.x -= (float) sideOffset(sidePosition);
                        position.y += (float) sideOffset(sidePosition + 1);
                        break;
                    case ANIM_DOWN:
                        position.x += (float) sideOffset(sidePosition);
                        position.y -= (float) sideOffset(sidePosition + 1);
                        break;
                }
            }
        } else if(goUp) {
            int side = sidePosition;
            if (animationState == ANIM_RIGHT) {
                side++;
                if(side > 3)
                    side = 0;
            }
            switch(side) {
                case SIDE_TOP:
                    position.x++;
                    position.y++;
                    break;
                case SIDE_RIGHT:
                    position.x--;
                    position.y++;
                    break;
                case SIDE_BOTTOM:
                    position.x--;
                    position.y--;
                    break;
                case SIDE_LEFT:
                    position.x++;
                    position.y--;
                    break;
            }
            if (animationState == ANIM_LEFT) {
                sidePosition++;
                if (sidePosition > 3)
                    sidePosition = 0;
            } else {
                sidePosition--;
                if (sidePosition < 0)
                    sidePosition = 3;
            }
        } else if(goFlat) {
            int side = sidePosition;
            if (animationState == ANIM_RIGHT) {
                side+=2;
                side %= 4;
            }
            switch(side) {
                case SIDE_TOP:
                    position.x++;
                    break;
                case SIDE_RIGHT:
                    position.y++;
                    break;
                case SIDE_BOTTOM:
                    position.x--;
                    break;
                case SIDE_LEFT:
                    position.y--;
                    break;
            }

        } else {
            if (animationState == ANIM_LEFT) {
                sidePosition--;
                if (sidePosition < 0)
                    sidePosition = 3;
            } else {
                sidePosition++;
                if (sidePosition > 3)
                    sidePosition = 0;
            }
        }
        switch (sidePosition) {
            case SIDE_TOP:
                positionOffset.x = 0;
                positionOffset.y = PLAYER_POSITION_OFFSET;
                rotationOffset = 0;
                break;
            case SIDE_RIGHT:
                positionOffset.x = -PLAYER_POSITION_OFFSET;
                positionOffset.y = 0;
                rotationOffset = (float) Math.PI / 2;
                break;
            case SIDE_BOTTOM:
                positionOffset.x = 0;
                positionOffset.y = -PLAYER_POSITION_OFFSET;
                rotationOffset = (float) Math.PI;
                break;
            case SIDE_LEFT:
                positionOffset.x = PLAYER_POSITION_OFFSET;
                positionOffset.y = 0;
                rotationOffset = -(float) Math.PI / 2;
                break;
        }
        stopAnimation();
    }

    private void stopAnimation() {
        goUp = false;
        goFlat = false;
        animationState = ANIM_STAND;
        startAnimationState = ANIM_STAND;
        currentRotation = 0;
        animationOffset.x = 0;
        animationOffset.y = 0;
    }

    private int sideOffset(int side) {
        int offset = -1;

        side %= 4;

        if(side < 0)
            side += 4;

        switch(side) {
            case SIDE_TOP:
                offset = 0;
                break;
            case SIDE_RIGHT:
                offset = 1;
                break;
            case SIDE_BOTTOM:
                offset = 0;
                break;
            case SIDE_LEFT:
                offset = -1;
                break;
        }

        return offset;
    }

    private float flatMove(float rot) {
        return (float)Math.sin(rot/2);
    }

    private float curveMove(float rot) {
        if(rot < 0)
            return ((float)1-PLAYER_POSITION_OFFSET)/(float)(1+Math.exp((rot+1.5f)*4));
        return -((float)1-PLAYER_POSITION_OFFSET)/(float)(1+Math.exp(-(rot-1.5f)*4));
    }

    private float directMove(float rot) {
        if(rot < 0)
            return PLAYER_POSITION_OFFSET/(float)(1+Math.exp((rot+1)*4));
        return -PLAYER_POSITION_OFFSET/(float)(1+Math.exp((-rot+1)*4));
    }

    private float delayedMove(float rot) {
        if(rot < 0)
            return -PLAYER_POSITION_OFFSET/(float)(1+Math.exp((rot+2)*4));
        return -PLAYER_POSITION_OFFSET/(float)(1+Math.exp((-rot+2)*4));
    }

    private float rotationFunction(float rot) {
        if(rot < 0)
            return -((float)Math.PI/2)/(float)(1+Math.exp((rot+1.5f)*4));
        return ((float)Math.PI/2)/(float)(1+Math.exp(-(rot-1.5f)*4));
    }

    public Vector2f getPosition() {
        return new Vector2f(position.x + positionOffset.x + animationOffset.x, position.y + positionOffset.y + animationOffset.y);
    }
}
