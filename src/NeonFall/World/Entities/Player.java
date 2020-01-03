// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World.Entities;

import java.util.LinkedList;
import org.joml.Matrix4f;
import NeonFall.Manager.InputManager;
import org.joml.Vector3f;
import org.joml.Matrix3f;
import org.joml.Vector4f;
import NeonFall.Manager.ResourceManager;
import NeonFall.World.RoundData;
import org.joml.Vector2f;
import NeonFall.Physics.AABB;

public class Player extends TexturedEntity
{
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
    private static final float ROTATION_SPEED = 10.0f;
    public static final float PLAYER_POSITION_Z = 2.0f;
    private static final float PLAYER_POSITION_OFFSET = 0.625f;
    private static final float FREEFALLCHARGE_LOOSE_PER_SECOND = 10.0f;
    private static final float FREEFALLCHARGE_GAIN_PER_SECOND = 20.0f;
    public static final float MAX_FREEFALLCHARGE = 50.0f;
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
    private float timer;
    private float freefallCharge;
    
    public Player(final RoundData roundData, final Vector2f position) {
        super(ResourceManager.PLAYER_MODEL_FILE, ResourceManager.TEX_SHIP_FILE, new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        this.timer = 0.0f;
        this.freefall = false;
        this.goUp = false;
        this.goFlat = false;
        this.roundData = roundData;
        this.position = position;
        this.sidePosition = 0;
        this.animationState = 0;
        this.startAnimationState = 0;
        this.animationOffset = new Vector2f(0.0f, 0.0f);
        this.positionOffset = new Vector2f(0.0f, 0.625f);
        this.rotationOffset = 0.0f;
        this.currentRotation = 0.0f;
        this.material.getDiffuse().set(1.0f, 0.0f, 0.0f);
        this.freefallCharge = 50.0f;
        final Matrix3f mat = new Matrix3f().scale(0.0625f, 0.0625f, 0.25f);
        this.boundingBox = new AABB(mat.transform(new Vector3f(-1.0f, -1.0f, -1.0f)), mat.transform(new Vector3f(1.0f, 1.0f, 1.0f)));
    }
    
    @Override
    public void update(final float delta) {
        this.timer += delta;
        if (this.timer > 1.0f) {
            this.timer %= 1.0f;
            System.out.println("Position: " + this.position.x + ":" + this.position.y);
            System.out.println("Side: " + this.sidePosition);
            System.out.println("Freefall: " + (this.freefall ? "On" : "Off"));
        }
        if (this.freefall) {
            float extraRot = 0.0f;
            this.freefallCharge -= delta * 10.0f;
            if (this.animationState == 0) {
                Vector3f selector = null;
                if (InputManager.isKeyDown(87)) {
                    this.animationState = 3;
                    selector = new Vector3f(0.0f, 1.0f, 0.0f);
                }
                else if (InputManager.isKeyDown(83)) {
                    this.animationState = 4;
                    selector = new Vector3f(0.0f, -1.0f, 0.0f);
                }
                else if (InputManager.isKeyDown(68)) {
                    this.animationState = 2;
                    selector = new Vector3f(-1.0f, 0.0f, 0.0f);
                }
                else if (InputManager.isKeyDown(65)) {
                    this.animationState = 1;
                    selector = new Vector3f(1.0f, 0.0f, 0.0f);
                }
                if (selector != null) {
                    final Matrix3f rotMat = new Matrix3f().rotateZ(this.rotationOffset);
                    rotMat.transform(selector);
                    selector.add(this.position.x, this.position.y, 0.0f);
                    this.goUp = this.roundData.isBlocked((int)selector.x - this.sideOffset(this.sidePosition), (int)selector.y + this.sideOffset(this.sidePosition + 1));
                }
            }
            else {
                if (this.animationState % 2 == 0) {
                    this.currentRotation += 10.0f * delta;
                }
                else {
                    this.currentRotation -= 10.0f * delta;
                }
                if (this.currentRotation < -3.1415927f || this.currentRotation > 3.1415927f) {
                    this.updatePlayerStates();
                }
                if (this.goUp) {
                    switch (this.animationState) {
                        case 3: {
                            this.animationOffset.y = -this.flatMove(this.currentRotation) * 0.625f * this.sideOffset(this.sidePosition + 1);
                            this.animationOffset.x = this.flatMove(this.currentRotation) * 0.625f * this.sideOffset(this.sidePosition);
                            this.roundData.camera.setLookUpRotationXZ(this.currentRotation + this.rotationOffset);
                            extraRot = this.currentRotation;
                            break;
                        }
                        case 2: {
                            this.animationOffset.x = this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1) + this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = -this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1) + this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.roundData.camera.setLookUpRotationXZ(-this.currentRotation / 2.0f + this.rotationOffset);
                            extraRot = -this.currentRotation / 2.0f;
                            break;
                        }
                        case 4: {
                            this.animationOffset.x = -(float)(1.0 - Math.sin(this.currentRotation / 2.0f)) * 0.25f * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = (float)(1.0 - Math.sin(this.currentRotation / 2.0f)) * 0.25f * this.sideOffset(this.sidePosition + 1);
                            break;
                        }
                        case 1: {
                            this.animationOffset.x = this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1) - this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1) + this.curveMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.roundData.camera.setLookUpRotationXZ(-this.currentRotation / 2.0f + this.rotationOffset);
                            extraRot = -this.currentRotation / 2.0f;
                            break;
                        }
                    }
                    this.modelMatrix = new Matrix4f().translate(this.position.x + this.animationOffset.x + this.positionOffset.x, this.position.y + this.animationOffset.y + this.positionOffset.y, 2.0f).rotateZ(this.rotationOffset + extraRot).scale(0.5f, 0.5f, 0.5f);
                    this.roundData.camera.setCamPos(this.position.x + this.positionOffset.x - (float)Math.sin(this.rotationOffset) * 0.5f, this.position.y + this.positionOffset.y + (float)Math.cos(this.rotationOffset) * 0.5f, 0.0f);
                }
                else {
                    switch (this.animationState) {
                        case 1: {
                            this.animationOffset.x = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1);
                            this.animationOffset.y = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            break;
                        }
                        case 2: {
                            this.animationOffset.x = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1);
                            this.animationOffset.y = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            break;
                        }
                        case 3: {
                            this.animationOffset.x = this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1);
                            break;
                        }
                        case 4: {
                            this.animationOffset.x = this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = -this.flatMove(this.currentRotation) * this.sideOffset(this.sidePosition + 1);
                            break;
                        }
                        case 5: {
                            this.animationOffset.x = (float)Math.sin(this.currentRotation / 2.0f) * 0.0625f * this.sideOffset(this.sidePosition);
                            this.animationOffset.y = -(float)Math.sin(this.currentRotation / 2.0f) * 0.0625f * this.sideOffset(this.sidePosition + 1);
                            break;
                        }
                    }
                    final Vector2f animationOffset = this.animationOffset;
                    animationOffset.x -= (float)Math.sin(this.rotationOffset) * 0.25f;
                    final Vector2f animationOffset2 = this.animationOffset;
                    animationOffset2.y += (float)Math.cos(this.rotationOffset) * 0.25f;
                    this.modelMatrix = new Matrix4f().translate(this.position.x + this.animationOffset.x + this.positionOffset.x, this.position.y + this.animationOffset.y + this.positionOffset.y, 2.0f).rotateZ(this.rotationOffset + extraRot).scale(0.5f, 0.5f, 0.5f);
                    this.roundData.camera.setCamPos(this.position.x + this.animationOffset.x + this.positionOffset.x - (float)Math.sin(this.rotationOffset) * 0.5f, this.position.y + this.animationOffset.y + this.positionOffset.y + (float)Math.cos(this.rotationOffset) * 0.5f, 0.0f);
                }
            }
        }
        else {
            this.freefallCharge += 20.0f * delta;
            if (this.freefallCharge > 50.0f) {
                this.freefallCharge = 50.0f;
            }
            if (InputManager.isKeyDown(87) || (!this.roundData.isBlocked((int)this.position.x, (int)this.position.y) && this.animationState == 0)) {
                this.freefall = true;
                this.animationState = 5;
            }
            else if (InputManager.isKeyDown(65) || InputManager.isKeyDown(68)) {
                if (InputManager.isKeyDown(65)) {
                    this.animationState = 1;
                }
                else {
                    this.animationState = 2;
                }
                if (this.startAnimationState == 0) {
                    this.startAnimationState = this.animationState;
                    int side = this.sidePosition;
                    if (this.animationState == 2 && ++side > 3) {
                        side = 0;
                    }
                    switch (side) {
                        case 0: {
                            this.goUp = this.roundData.isBlocked((int)this.position.x + 1, (int)this.position.y + 1);
                            break;
                        }
                        case 1: {
                            this.goUp = this.roundData.isBlocked((int)this.position.x - 1, (int)this.position.y + 1);
                            break;
                        }
                        case 2: {
                            this.goUp = this.roundData.isBlocked((int)this.position.x - 1, (int)this.position.y - 1);
                            break;
                        }
                        case 3: {
                            this.goUp = this.roundData.isBlocked((int)this.position.x + 1, (int)this.position.y - 1);
                            break;
                        }
                    }
                    if (this.animationState == 2) {
                        side = ++side % 4;
                    }
                    switch (side) {
                        case 0: {
                            this.goFlat = this.roundData.isBlocked((int)this.position.x + 1, (int)this.position.y);
                            break;
                        }
                        case 1: {
                            this.goFlat = this.roundData.isBlocked((int)this.position.x, (int)this.position.y + 1);
                            break;
                        }
                        case 2: {
                            this.goFlat = this.roundData.isBlocked((int)this.position.x - 1, (int)this.position.y);
                            break;
                        }
                        case 3: {
                            this.goFlat = this.roundData.isBlocked((int)this.position.x, (int)this.position.y - 1);
                            break;
                        }
                    }
                }
            }
            if (this.animationState == 1) {
                this.currentRotation -= 10.0f * delta;
            }
            else if (this.animationState == 2) {
                this.currentRotation += 10.0f * delta;
            }
            if (this.startAnimationState == 1) {
                if (this.animationState == 1 && this.currentRotation < -3.1415927f) {
                    this.updatePlayerStates();
                }
                else if (this.currentRotation > 0.0f) {
                    this.stopAnimation();
                }
            }
            else if (this.startAnimationState == 2) {
                if (this.animationState == 2 && this.currentRotation > 3.1415927f) {
                    this.updatePlayerStates();
                }
                else if (this.currentRotation < 0.0f) {
                    this.stopAnimation();
                }
            }
            if (this.goUp) {
                int side = this.sidePosition;
                if (this.animationState == 2 && --side < 0) {
                    side = 3;
                }
                switch (side) {
                    case 0: {
                        this.animationOffset.x = this.curveMove(this.currentRotation);
                        this.animationOffset.y = this.curveMove(this.currentRotation);
                        break;
                    }
                    case 1: {
                        this.animationOffset.x = -this.curveMove(this.currentRotation);
                        this.animationOffset.y = this.curveMove(this.currentRotation);
                        break;
                    }
                    case 2: {
                        this.animationOffset.x = -this.curveMove(this.currentRotation);
                        this.animationOffset.y = -this.curveMove(this.currentRotation);
                        break;
                    }
                    case 3: {
                        this.animationOffset.x = this.curveMove(this.currentRotation);
                        this.animationOffset.y = -this.curveMove(this.currentRotation);
                        break;
                    }
                }
                final float rot = this.rotationFunction(this.currentRotation) + this.rotationOffset;
                this.modelMatrix = new Matrix4f().translate(this.position.x + this.animationOffset.x + this.positionOffset.x, this.position.y + this.animationOffset.y + this.positionOffset.y, 2.0f).rotateZ(-rot).scale(0.5f, 0.5f, 0.5f);
                if (this.sidePosition % 2 == 0) {
                    this.roundData.camera.setCamPos(this.position.x + this.positionOffset.x + (float)Math.sin(rot) * 0.125f, this.position.y + this.positionOffset.y + this.animationOffset.y + (float)Math.cos(rot) * 0.5f, 0.0f);
                }
                else {
                    this.roundData.camera.setCamPos(this.position.x + this.positionOffset.x + this.animationOffset.x - (float)Math.sin(rot) * 0.5f, this.position.y + this.positionOffset.y - (float)Math.cos(rot) * 0.125f, 0.0f);
                }
                this.roundData.camera.setLookUpRotationXZ(-this.currentRotation / 2.0f + this.rotationOffset);
            }
            else if (this.goFlat) {
                switch (this.sidePosition) {
                    case 0: {
                        this.animationOffset.x = -this.flatMove(this.currentRotation);
                        break;
                    }
                    case 1: {
                        this.animationOffset.y = -this.flatMove(this.currentRotation);
                        break;
                    }
                    case 2: {
                        this.animationOffset.x = this.flatMove(this.currentRotation);
                        break;
                    }
                    case 3: {
                        this.animationOffset.y = this.flatMove(this.currentRotation);
                        break;
                    }
                }
                final float rot2 = this.rotationOffset;
                this.modelMatrix = new Matrix4f().translate(this.position.x + this.animationOffset.x + this.positionOffset.x, this.position.y + this.animationOffset.y + this.positionOffset.y, 2.0f).rotateZ(rot2).scale(0.5f, 0.5f, 0.5f);
                this.roundData.camera.setCamPos(this.position.x + this.animationOffset.x + this.positionOffset.x - (float)Math.sin(rot2) * 0.5f, this.position.y + this.animationOffset.y + this.positionOffset.y + (float)Math.cos(rot2) * 0.5f, 0.0f);
                this.roundData.camera.setLookUpRotationXZ(this.rotationOffset);
            }
            else {
                switch (this.sidePosition) {
                    case 0: {
                        this.animationOffset.x = this.directMove(this.currentRotation);
                        this.animationOffset.y = this.delayedMove(this.currentRotation);
                        break;
                    }
                    case 1: {
                        this.animationOffset.x = -this.delayedMove(this.currentRotation);
                        this.animationOffset.y = this.directMove(this.currentRotation);
                        break;
                    }
                    case 2: {
                        this.animationOffset.x = -this.directMove(this.currentRotation);
                        this.animationOffset.y = -this.delayedMove(this.currentRotation);
                        break;
                    }
                    case 3: {
                        this.animationOffset.x = this.delayedMove(this.currentRotation);
                        this.animationOffset.y = -this.directMove(this.currentRotation);
                        break;
                    }
                }
                final float rot2 = this.rotationFunction(this.currentRotation) + this.rotationOffset;
                this.modelMatrix = new Matrix4f().translate(this.position.x + this.animationOffset.x + this.positionOffset.x, this.position.y + this.animationOffset.y + this.positionOffset.y, 2.0f).rotateZ(rot2).scale(0.5f, 0.5f, 0.5f);
                this.roundData.camera.setCamPos(this.position.x + this.animationOffset.x + this.positionOffset.x - (float)Math.sin(rot2) * 0.5f, this.position.y + this.animationOffset.y + this.positionOffset.y + (float)Math.cos(rot2) * 0.5f, 0.0f);
                this.roundData.camera.setLookUpRotationXZ(this.currentRotation / 2.0f + this.rotationOffset);
            }
        }
        final Vector3f bbPos = new Vector3f(this.position.x + this.positionOffset.x + this.animationOffset.x * 1.125f, this.position.y + this.positionOffset.y + this.animationOffset.y * 1.125f, 2.0f);
        this.boundingBox.setPosition(bbPos);
        for (int x = (int)bbPos.x - 1; x < bbPos.x + 1.0f; ++x) {
            for (int y = (int)bbPos.y; y - 1 < bbPos.y + 1.0f; ++y) {
                if (this.roundData.isInMap(x, y)) {
                    final LinkedList<BarEntity> barEntities = this.roundData.barEntities[x][y];
                    if (barEntities.size() > 0 && barEntities.getFirst().isColliding(this.boundingBox)) {
                        this.roundData.loosed = true;
                    }
                }
            }
        }
        if (this.freefallCharge < 0.0f) {
            this.roundData.loosed = true;
        }
    }
    
    public void updatePlayerStates() {
        if (this.freefall) {
            if (this.goUp) {
                switch (this.animationState) {
                    case 1: {
                        final Vector2f position = this.position;
                        position.x += this.sideOffset(this.sidePosition + 1) - this.sideOffset(this.sidePosition);
                        final Vector2f position2 = this.position;
                        position2.y += this.sideOffset(this.sidePosition + 1) + this.sideOffset(this.sidePosition);
                        ++this.sidePosition;
                        break;
                    }
                    case 2: {
                        final Vector2f position3 = this.position;
                        position3.x -= this.sideOffset(this.sidePosition + 1) + this.sideOffset(this.sidePosition);
                        final Vector2f position4 = this.position;
                        position4.y -= this.sideOffset(this.sidePosition) - this.sideOffset(this.sidePosition + 1);
                        --this.sidePosition;
                        break;
                    }
                    case 3: {
                        final Vector2f position5 = this.position;
                        position5.x -= this.sideOffset(this.sidePosition) * 2;
                        final Vector2f position6 = this.position;
                        position6.y += this.sideOffset(this.sidePosition + 1) + this.sideOffset(this.sidePosition + 1);
                        this.sidePosition += 2;
                        break;
                    }
                }
                this.sidePosition %= 4;
                if (this.sidePosition < 0) {
                    this.sidePosition = 3;
                }
                this.freefall = false;
            }
            else {
                switch (this.animationState) {
                    case 1: {
                        final Vector2f position7 = this.position;
                        position7.x += this.sideOffset(this.sidePosition + 1);
                        final Vector2f position8 = this.position;
                        position8.y += this.sideOffset(this.sidePosition);
                        break;
                    }
                    case 2: {
                        final Vector2f position9 = this.position;
                        position9.x -= this.sideOffset(this.sidePosition + 1);
                        final Vector2f position10 = this.position;
                        position10.y -= this.sideOffset(this.sidePosition);
                        break;
                    }
                    case 3: {
                        final Vector2f position11 = this.position;
                        position11.x -= this.sideOffset(this.sidePosition);
                        final Vector2f position12 = this.position;
                        position12.y += this.sideOffset(this.sidePosition + 1);
                        break;
                    }
                    case 4: {
                        final Vector2f position13 = this.position;
                        position13.x += this.sideOffset(this.sidePosition);
                        final Vector2f position14 = this.position;
                        position14.y -= this.sideOffset(this.sidePosition + 1);
                        break;
                    }
                }
            }
        }
        else if (this.goUp) {
            int side = this.sidePosition;
            if (this.animationState == 2 && ++side > 3) {
                side = 0;
            }
            switch (side) {
                case 0: {
                    final Vector2f position15 = this.position;
                    ++position15.x;
                    final Vector2f position16 = this.position;
                    ++position16.y;
                    break;
                }
                case 1: {
                    final Vector2f position17 = this.position;
                    --position17.x;
                    final Vector2f position18 = this.position;
                    ++position18.y;
                    break;
                }
                case 2: {
                    final Vector2f position19 = this.position;
                    --position19.x;
                    final Vector2f position20 = this.position;
                    --position20.y;
                    break;
                }
                case 3: {
                    final Vector2f position21 = this.position;
                    ++position21.x;
                    final Vector2f position22 = this.position;
                    --position22.y;
                    break;
                }
            }
            if (this.animationState == 1) {
                ++this.sidePosition;
                if (this.sidePosition > 3) {
                    this.sidePosition = 0;
                }
            }
            else {
                --this.sidePosition;
                if (this.sidePosition < 0) {
                    this.sidePosition = 3;
                }
            }
        }
        else if (this.goFlat) {
            int side = this.sidePosition;
            if (this.animationState == 2) {
                side += 2;
                side %= 4;
            }
            switch (side) {
                case 0: {
                    final Vector2f position23 = this.position;
                    ++position23.x;
                    break;
                }
                case 1: {
                    final Vector2f position24 = this.position;
                    ++position24.y;
                    break;
                }
                case 2: {
                    final Vector2f position25 = this.position;
                    --position25.x;
                    break;
                }
                case 3: {
                    final Vector2f position26 = this.position;
                    --position26.y;
                    break;
                }
            }
        }
        else if (this.animationState == 1) {
            --this.sidePosition;
            if (this.sidePosition < 0) {
                this.sidePosition = 3;
            }
        }
        else {
            ++this.sidePosition;
            if (this.sidePosition > 3) {
                this.sidePosition = 0;
            }
        }
        switch (this.sidePosition) {
            case 0: {
                this.positionOffset.x = 0.0f;
                this.positionOffset.y = 0.625f;
                this.rotationOffset = 0.0f;
                break;
            }
            case 1: {
                this.positionOffset.x = -0.625f;
                this.positionOffset.y = 0.0f;
                this.rotationOffset = 1.5707964f;
                break;
            }
            case 2: {
                this.positionOffset.x = 0.0f;
                this.positionOffset.y = -0.625f;
                this.rotationOffset = 3.1415927f;
                break;
            }
            case 3: {
                this.positionOffset.x = 0.625f;
                this.positionOffset.y = 0.0f;
                this.rotationOffset = -1.5707964f;
                break;
            }
        }
        this.stopAnimation();
    }
    
    private void stopAnimation() {
        this.goUp = false;
        this.goFlat = false;
        this.animationState = 0;
        this.startAnimationState = 0;
        this.currentRotation = 0.0f;
        this.animationOffset.x = 0.0f;
        this.animationOffset.y = 0.0f;
    }
    
    private int sideOffset(int side) {
        int offset = -1;
        side %= 4;
        if (side < 0) {
            side += 4;
        }
        switch (side) {
            case 0: {
                offset = 0;
                break;
            }
            case 1: {
                offset = 1;
                break;
            }
            case 2: {
                offset = 0;
                break;
            }
            case 3: {
                offset = -1;
                break;
            }
        }
        return offset;
    }
    
    private float flatMove(final float rot) {
        return (float)Math.sin(rot / 2.0f);
    }
    
    private float curveMove(final float rot) {
        if (rot < 0.0f) {
            return 0.375f / (float)(1.0 + Math.exp((rot + 1.5f) * 4.0f));
        }
        return -0.375f / (float)(1.0 + Math.exp(-(rot - 1.5f) * 4.0f));
    }
    
    private float directMove(final float rot) {
        if (rot < 0.0f) {
            return 0.625f / (float)(1.0 + Math.exp((rot + 1.0f) * 4.0f));
        }
        return -0.625f / (float)(1.0 + Math.exp((-rot + 1.0f) * 4.0f));
    }
    
    private float delayedMove(final float rot) {
        if (rot < 0.0f) {
            return -0.625f / (float)(1.0 + Math.exp((rot + 2.0f) * 4.0f));
        }
        return -0.625f / (float)(1.0 + Math.exp((-rot + 2.0f) * 4.0f));
    }
    
    private float rotationFunction(final float rot) {
        if (rot < 0.0f) {
            return -1.5707964f / (float)(1.0 + Math.exp((rot + 1.5f) * 4.0f));
        }
        return 1.5707964f / (float)(1.0 + Math.exp(-(rot - 1.5f) * 4.0f));
    }
    
    public Vector2f getPosition() {
        return new Vector2f(this.position.x + this.positionOffset.x + this.animationOffset.x, this.position.y + this.positionOffset.y + this.animationOffset.y);
    }
    
    public float getFreefallCharge() {
        return this.freefallCharge;
    }
}
