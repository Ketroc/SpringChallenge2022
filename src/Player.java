import java.util.*;
import java.util.function.Predicate;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    public static HeroWrap heroWrap;
    public static EnemyHeroWrap enemyHeroWrap;
    public static MonsterWrap monsterWrap;
    public static Base myBase;
    public static Base enemyBase;
    public static Point myBasePos;
    public static Point enemyBasePos;
    public static List<List<Point>> heroIdlePosList;
    public static int step = 0;
    public static int monsterHp = 1;
    public static boolean didEnemyControlMe;

    public enum Type {
        ME,
        ENEMY,
        MONSTER
    }

    public enum ThreatTo {
        ME,
        ENEMY,
        NONE
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        myBasePos = new Point(in.nextInt(), in.nextInt());
        System.err.println(myBasePos);
        //setPosConstants_DefenseOnly();
        setPosConstants();
        int numHeroes = in.nextInt(); // Always 3

        heroWrap = new HeroWrap();
        monsterWrap = new MonsterWrap();
        enemyHeroWrap = new EnemyHeroWrap();

        // game loop
        while (true) {
            //base info
            for (int i = 0; i < 2; i++) {
                int health = in.nextInt(); // base health
                int mana = in.nextInt(); // Ignore in the first league; Spend ten mana to cast a spell
                if (i == 0) {
                    myBase = new Base(health, mana, myBasePos);
                }
                else {
                    enemyBase = new Base(health, mana, enemyBasePos);
                }
            }

            //build units lists
            enemyHeroWrap.enemyHeroes.clear();
            monsterWrap.monsters.clear();
            int numUnits = in.nextInt(); // Amount of heros and monsters you can see
            for (int i = 0; i < numUnits; i++) {
                int id = in.nextInt(); // Unique identifier
                Type type = getType(in.nextInt()); // 0=monster, 1=your hero, 2=opponent hero
                int x = in.nextInt(); // Position of this unit
                int y = in.nextInt();
                int shieldLife = in.nextInt(); // Ignore for this league; Count down until shield spell fades
                int isControlled = in.nextInt(); // Ignore for this league; Equals 1 when this unit is under a control spell
                int health = in.nextInt(); // Remaining health of this monster
                int vx = in.nextInt(); // Trajectory of this monster
                int vy = in.nextInt();
                int nearBase = in.nextInt(); // 0=monster with no target yet, 1=monster targeting a base
                int threatFor = in.nextInt(); // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

                switch(type) {
                    case ME:
                        heroWrap.add(id, x, y, shieldLife, isControlled, health, nearBase);
                        break;
                    case ENEMY:
                        enemyHeroWrap.add(id, x, y, shieldLife, isControlled, health, nearBase);
                        break;
                    case MONSTER:
                        monsterWrap.add(id, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor);
                        break;
                }
            }

            //MAIN LOGIC
            setMonsterHp();
            System.err.println("MY BASE");
            System.err.println("=======");
            System.err.println(myBase);
            System.err.println("ENEMY BASE");
            System.err.println("==========");
            System.err.println(enemyBase);

            System.err.println("===> prior to onstepstart");
            heroWrap.print();
            heroWrap.onStepStart();

            System.err.println("===> prior to assign target");
            heroWrap.print();
            heroWrap.assignTarget();

            System.err.println("===> prior to onstepend");
            heroWrap.print();
            heroWrap.onStepEnd();

            System.err.println("===> after onstepend");
            heroWrap.print();

            step++;
        }
    }

    private static void setMonsterHp() {
        monsterHp = Math.max(
                monsterHp,
                monsterWrap.monsters.stream()
                        .max(Comparator.comparing(monster -> monster.hp))
                        .map(monster -> monster.hp)
                        .orElse(0)
        );
    }

    public static Type getType(int type) {
        switch (type) {
            case 1:
                return Type.ME;
            case 2:
                return Type.ENEMY;
            default:
                return Type.MONSTER;
        }
    }

    public static void setPosConstants() {
        heroIdlePosList = new ArrayList<>();
        if (myBasePos.x < 8000) { //top left
            enemyBasePos = new Point(17630, 9000);

            //bottom
            List<Point> bottom = new ArrayList<>();
            bottom.add(new Point(5000, 4500));
            bottom.add(new Point(1900, 6500));
            heroIdlePosList.add(bottom);

            //right
            List<Point> right = new ArrayList<>();
            right.add(new Point(7500, 1900));
            right.add(new Point(5500, 3500));
            heroIdlePosList.add(right);

            //middle
            Point enemyBaseCenterPos = Base.getCenterBasePos(enemyBasePos);
            List<Point> middle = new ArrayList<>();
            middle.add(new Point(9500, 6500));
            middle.add(new Point(13000, 2500));
            middle.add(enemyBaseCenterPos);
            middle.add(new Point(13000, 2500));
            middle.add(new Point(9500, 6500));
            middle.add(enemyBaseCenterPos);
            heroIdlePosList.add(middle);
        }
        else {
            enemyBasePos = new Point(0, 0);

            //top
            List<Point> top = new ArrayList<>();
            top.add(new Point(13500, 3800));
            top.add(new Point(15200, 1500));
            heroIdlePosList.add(top);

            //left
            List<Point> left = new ArrayList<>();
            left.add(new Point(9500, 6900));
            left.add(new Point(12000, 5000));
            heroIdlePosList.add(left);

            //middle
            Point enemyBaseCenterPos = Base.getCenterBasePos(enemyBasePos);
            List<Point> middle = new ArrayList<>();
            middle.add(new Point(8000, 2000));
            middle.add(new Point(5000, 6500));
            middle.add(enemyBaseCenterPos);
            middle.add(new Point(5000, 6500));
            middle.add(new Point(8000, 2000));
            middle.add(enemyBaseCenterPos);
            heroIdlePosList.add(middle);
        }
    }

//    public static void setPosConstants_DefenseOnly() {
//        heroIdlePosList = new ArrayList<>();
//        if (myBasePos.x < 8000) { //top left
//            heroIdlePosList.add(new Point(2400, 6500));//bottom
//            heroIdlePosList.add(new Point(8750, 1600));//right
//            heroIdlePosList.add(new Point(6500, 4500));//middle
//            enemyBasePos = new Point(17630, 9000);
//        }
//        else {
//            heroIdlePosList.add(new Point(15000, 1700)); //top
//            heroIdlePosList.add(new Point(10300, 7000)); //left
//            heroIdlePosList.add(new Point(11300, 3600)); //middle
//            enemyBasePos = new Point(0, 0);
//        }
//    }

    public static class Unit {
        int id;
        Point pos;
        int shields;
        boolean isControlled;
        int hp;
        boolean isNearBase;

        public Unit(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            this.id = id;
            this.pos = new Point(x, y);
            this.shields = shieldLife;
            this.isControlled = isControlled == 1;
            this.hp = health;
            this.isNearBase = nearBase == 1;
        }

        public boolean isInMyZone() {
            return isNearBase && pos.distance(myBasePos) < Base.ZONE_RANGE;
        }

        @Override
        public String toString() {
            return "\nid = " + id +
                    "\npos = " + pos.toString() + "\n";
        }
    }

    public static class Monster extends Unit {
        static int SPEED = 400;

        ThreatTo threatTo;
        Point vector;

        public Monster(int id, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
            setThreatTo(threatFor);
            this.vector = new Point(vx, vy);
        }

        public void setThreatTo(int threatFor) {
            switch (threatFor) {
                case 1:
                    threatTo = ThreatTo.ME;
                    break;
                case 2:
                    threatTo = ThreatTo.ENEMY;
                    break;
                default:
                    threatTo = ThreatTo.NONE;
            }
        }

        public boolean isTargeted() {
            return heroWrap.containsTarget(id);
        }

        @Override
        public String toString() {
            return "Monster\nthreatTo = " + threatTo +
                    "\nnearBase = " + isNearBase +
                    super.toString();
        }
    }

    public static class Hero extends Unit {
        static int DAMAGE = 2;
        static int SPEED = 800;
        static int ATTACK_RANGE = 300;
        static int CONTROL_RANGE = 2199;
        static int SHIELD_RANGE = 2199;
        static int VISION_RANGE = 2199;
        static int WIND_RANGE = 1279;
        static int WIND_DISTANCE = 2199;

        static boolean isWindCast;

        Point idlePos;
        int targetId = -1;
        List<Point> patrolPoints;
        int curPatrolIndex;
        int prevWindFrame;

        public Hero(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
            patrolPoints = heroIdlePosList.remove(0);
            idlePos = patrolPoints.get(0);
        }

        public void update(int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            this.pos = new Point(x, y);
            this.shields = shieldLife;
            this.isControlled = isControlled == 1;
            this.hp = health;
            this.isNearBase = nearBase == 1;
        }

        public Point getIdlePos() {
            return idlePos;
        }

        public Point getInterceptPos(Monster targetMonster) {
            Point interceptPos = targetMonster.pos;
            Point prevInterceptPos = interceptPos;
            while (Point.inBounds(interceptPos) &&
                    pos.distance(interceptPos) / 2 > targetMonster.pos.distance(interceptPos)) {
                prevInterceptPos = interceptPos;
                interceptPos = interceptPos.add(targetMonster.vector);
            }
            return prevInterceptPos;
        }

        public void patrol() {
            patrol("PATROL");
        }

        public void patrol(String message) {
            if (getIdlePos().distance(pos) < 1) {
                nextPatrolPos();
            }
            System.out.println("MOVE " + (int)getIdlePos().x + " " + (int)getIdlePos().y + " " + message);
        }

        private void nextPatrolPos() {
            int numPatrolPoints = (monsterHp < 18) ? 2 : patrolPoints.size();
            curPatrolIndex = (curPatrolIndex + 1) % numPatrolPoints;
            idlePos = patrolPoints.get(curPatrolIndex);
        }


        public void move(Point targetPos) {
            move(targetPos, "MOVE");
        }

        public void move(Point targetPos, String message) {
            System.out.println("MOVE " + (int)targetPos.x + " " + (int)targetPos.y + " " + message);
        }

        public void control(int targetId, Point controlToPos) {
            control(targetId, controlToPos, "CONTROL");
        }

        public void control(int targetId, Point controlToPos, String message) {
            System.out.println("SPELL CONTROL " + targetId + " " + (int)controlToPos.x + " " + (int)controlToPos.y + " " + message);
            myBase.mana -= 10;
        }

        public void wind(Point targetPos) {
            wind(targetPos, "WIND");
        }

        public void wind(Point targetPos, String message) {
            System.out.println("SPELL WIND " + (int)targetPos.x + " " + (int)targetPos.y + " " + message);
            prevWindFrame = step;
            myBase.mana -= 10;
            Hero.isWindCast = true;
        }

        public void shield(int targetId) {
            shield(targetId, "SHIELD");
        }
        public void shield(int targetId, String message) {
            System.out.println("SPELL SHIELD " + targetId + " " + message);
            myBase.mana -= 10;
        }

        public void onStepStart() {
            //remove dead targets
            if (targetId != -1 &&
                    (!monsterWrap.contains(targetId) || monsterWrap.get(targetId).threatTo != ThreatTo.ME)) {
                targetId = -1;
            }
        }

        public void onStepEnd() {

        }

        @Override
        public String toString() {
            return "\nidlePos = " + getIdlePos() +
                    super.toString();
        }
    }

    public static class DefenseHero extends Hero {
        public DefenseHero(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
        }

        @Override
        public void onStepStart() {
            super.onStepStart();
        }

        @Override
        public void onStepEnd() {
            //SHIELD my partner if controlled
            if (myBase.mana >= 10) {
                Hero controlledHero = heroWrap.getControlledHero(pos, SHIELD_RANGE);
                if (controlledHero != null) {
                    shield(controlledHero.id);
                    return;
                }
            }

//            //SHIELD self vs player who uses CONTROL
//            if (didEnemyControlMe &&
//                    shields == 0 &&
//                    enemyBase.mana >= 20 &&
//                    pos.distance(myBasePos) < 9000 &&
//                    enemyHeroWrap.isAnyNearby(pos, CONTROL_RANGE + SPEED)) {
//                shield(id);
//                return;
//            }

            //Locked on
            if (targetId != -1) {
                Monster targetMonster = monsterWrap.get(targetId);
                double distanceToBase = targetMonster.pos.distance(myBasePos);

                //switch to emergency target
                Monster scoringMonster = monsterWrap.getScoringMonster();
                if (scoringMonster != null) {
                    double monsterDistance = scoringMonster.pos.distance(pos);
                    if (monsterDistance < 7000 && (monsterDistance < 2000 ||
                            scoringMonster.pos.distance(myBasePos) + 2000 < targetMonster.pos.distance(myBasePos))) {
                        targetId = scoringMonster.id;
                    }
                }

                //CONTROL monster
                if (targetMonster.shields == 0 &&
                        distanceToBase >= 5000 &&
                        targetMonster.pos.distance(pos) <= CONTROL_RANGE &&
                        myBase.mana > 30 &&
                        targetMonster.hp > 13 &&
                        (monsterWrap.numAttackTargetsNearby(getIdlePos(), 4000) > 1 ||
                                monsterWrap.numAttackTargetsNearby(myBasePos, 5000) > 1)) {
                    Point controlToPos = targetMonster.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(targetMonster.pos) :
                            enemyBasePos;
                    control(targetId, controlToPos, targetMonster.vector.toString());
                    targetId = -1;
                    return;
                }

                //WIND monster
                if (!Hero.isWindCast &&
                        step > prevWindFrame + 1 &&
                        myBase.mana >= 10 &&
                        targetMonster.shields == 0 &&
                        targetMonster.threatTo == ThreatTo.ME &&
                        distanceToBase <= getWindRange() &&
                        targetMonster.pos.distance(pos) <= WIND_RANGE) {
                    wind(enemyBasePos);
                    return;
                }

                //ATTACK monster
                move(getInterceptPos(targetMonster), "DEFEND");
                return;
            }

            //attack nearest neutral monster
            Monster nearbyMonster = monsterWrap.closestTo(getIdlePos(), 3000, m -> m.threatTo != ThreatTo.ENEMY);
            if (nearbyMonster != null) {
                //CONTROL monster
                if (nearbyMonster.shields == 0 &&
                        nearbyMonster.hp > 15 &&
                        myBase.mana > 80 &&
                        nearbyMonster.pos.distance(pos) <= CONTROL_RANGE &&
                        monsterWrap.numAttackTargetsNearby(getIdlePos(), 3000) > 1) {
                    Point controlToPos = nearbyMonster.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(nearbyMonster.pos) :
                            enemyBasePos;
                    control(nearbyMonster.id, controlToPos, nearbyMonster.vector.toString());
                    return;
                }
                move(getInterceptPos(nearbyMonster), "FARM");
                return;
            }

            //go to idle position
            patrol();
        }

        private double getWindRange() {
            return Base.ZONE_RANGE;
//            return (monsterHp > 14 || enemyHeroWrap.isAnyNearby(pos, 3000)) ?
//                    Base.ZONE_RANGE :
//                    Monster.SPEED + Base.SCORE_RANGE;
        }

        @Override
        public String toString() {
            return "DefenseHero\ntargetId = " + targetId +
                    super.toString();
        }
    }

    public static class OffenseHero extends Hero {
        public OffenseHero(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
        }

        @Override
        public Point getIdlePos() {
            return isFinisherMode() ? enemyBase.centerPos : idlePos;
        }

        @Override
        public void onStepStart() {
            if (isFinisherMode()) {
                targetId = -1;
            }
            super.onStepStart();
        }

        @Override
        public void onStepEnd() {
            //WIND packs of offensive monsters
            if (myBase.mana > 30 &&
                    step > prevWindFrame + 1 &&
                    pos.distance(enemyBasePos) < 10000 &&
                    monsterWrap.numWindTargets(pos) > 3) {
                System.err.print("Offensive WIND");
                wind(enemyBasePos);
                return;
            }

            //Create 2+ new threats with WIND
            if (step > prevWindFrame + 1 &&
                    pos.distance(enemyBasePos) < 10000 &&
                    monsterWrap.numWindToEnemyZone(pos) >= 2) {
                System.err.print("Offensive WIND (make new threats)");
                wind(enemyBasePos);
                return;
            }

            //SHIELD offensive monster
            Monster shieldMonster = monsterWrap.getShieldTargets(pos, Base.ZONE_RANGE + Base.SCORE_RANGE);
            if (myBase.mana > 10 && shieldMonster != null) {
                System.err.println("Offensive Shield Target: " + shieldMonster);
                shield(shieldMonster.id);
                return;
            }

            //CONTROL enemy heroes
            if (myBase.mana > 50 &&
                    enemyBase.pos.distance(pos) < Base.ZONE_RANGE &&
                    monsterWrap.numMonstersNearby(enemyBasePos, Base.ZONE_RANGE) > 1) {
                EnemyHero enemyHero = enemyHeroWrap.getDeepestEnemyHero(pos, Hero.CONTROL_RANGE);
                if (enemyHero != null) {
                    control(enemyHero.id, pos.towards(enemyBasePos, -VISION_RANGE), "Dance, minion!");
                    return;
                }
            }

            //Locked on
            if (targetId != -1) {
                Monster targetMonster = monsterWrap.get(targetId);
                double distanceToBase = targetMonster.pos.distance(myBasePos);

                //CONTROL monster
                if (targetMonster.shields == 0 &&
                        distanceToBase >= 5000 &&
                        targetMonster.pos.distance(pos) <= CONTROL_RANGE &&
                        myBase.mana > 30 &&
                        targetMonster.hp > 13 &&
                        (monsterWrap.numAttackTargetsNearby(getIdlePos(), 4000) > 1 ||
                                monsterWrap.numAttackTargetsNearby(myBasePos, 5000) > 1)) {
                    Point controlToPos = targetMonster.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(targetMonster.pos) :
                            enemyBasePos;
                    control(targetId, controlToPos, targetMonster.vector.toString());
                    targetId = -1;
                    return;
                }

                //ATTACK monster
                move(getInterceptPos(targetMonster), "DEFEND");
                return;
            }

            //attack nearest neutral monster
            if ((monsterHp < 18 || myBase.mana < 150) && !isFinisherMode()) {
                Monster nearbyMonster = monsterWrap.closestTo(getIdlePos(), 3500, m -> m.threatTo != ThreatTo.ENEMY);
                if (nearbyMonster != null) {
                    //CONTROL monster
                    if (nearbyMonster.shields == 0 &&
                            nearbyMonster.hp > 13 &&
                            myBase.mana > 70 &&
                            nearbyMonster.pos.distance(pos) <= CONTROL_RANGE &&
                            monsterWrap.numAttackTargetsNearby(getIdlePos(), 3500) > 1) {
                        Point controlToPos = nearbyMonster.pos.distance(enemyBasePos) > 7500 ?
                                enemyBase.pickTargetCorner(nearbyMonster.pos) :
                                enemyBasePos;
                        control(nearbyMonster.id, controlToPos, nearbyMonster.vector.toString());
                        return;
                    }
                    move(getInterceptPos(nearbyMonster), "FARM");
                    return;
                }
            }

            //go to idle position
            patrol();
        }

        public boolean isFinisherMode() {
            return step > 210 ||
                    (myBase.mana >= 20 && myBase.lives <= enemyBase.lives && step > 190) ||
                    (myBase.mana > 20 && monsterWrap.numMonstersNearby(enemyBasePos, Base.ZONE_RANGE) > 2);
        }

        @Override
        public String toString() {
            return "OffenseHero\n" + super.toString();
        }
    }

    public static class EnemyHero extends Unit {
        static int DAMAGE = 2;
        static int SPEED = 800;
        static int CONTROL_RANGE = 2200;
        static int SHIELD_RANGE = 2200;
        static int VISION_RANGE = 2200;
        static int WIND_RANGE = 1280;
        static int WIND_DISTANCE = 2200;

        public EnemyHero(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
        }

        public void update(int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            this.pos = new Point(x, y);
            this.shields = shieldLife;
            this.isControlled = isControlled == 1;
            this.hp = health;
            this.isNearBase = nearBase == 1;
        }

        @Override
        public String toString() {
            return "EnemyHero\n" + super.toString();
        }
    }

    public static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distance(Point to) {
            double xDist = Math.abs(x - to.x);
            double yDist = Math.abs(y - to.y);
            return Math.sqrt(  xDist * xDist + yDist * yDist );
        }

        public Point add(Point toAdd) {
            return new Point(x + toAdd.x, y + toAdd.y);
        }

        public Point add(int xToAdd, int yToAdd) {
            return new Point(x + xToAdd, y + yToAdd);
        }

        public Point subtract(Point toSubtract) {
            return new Point(x - toSubtract.x, y - toSubtract.y);
        }

        public Point subtract(int xToSubtract, int yToSubtract) {
            return new Point(x - xToSubtract, y - yToSubtract);
        }

        public Point multiply(double distance) {
            return new Point(x * distance, y * distance);
        }

        public Point towards(Point targetPos, int distance) {
            Point vector = unitVector(targetPos);
            return this.add(vector.multiply(distance));
        }

        public Point unitVector(Point to) {
            return normalize(to.subtract(this));
        }

        public static boolean inBounds(Point pos) {
            return pos.x >= 0 && pos.y >= 0 && pos.x <= 17630 && pos.y <= 9000;
        }

        public static Point normalize(Point vector) {
            double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
            return new Point((vector.x / length), (vector.y / length));
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static class HeroWrap {
        public List<Hero> myHeroes = new ArrayList<>();

        public boolean containsHero(int heroId) {
            return myHeroes.stream().anyMatch(hero -> hero.id == heroId);
        }

        public boolean containsTarget(int monsterId) {
            return myHeroes.stream().anyMatch(hero -> hero.targetId == monsterId);
        }

        public void add(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            if (containsHero(id)) {
                update(id, x, y, shieldLife, isControlled, health, nearBase);
            }
            else if (heroIdlePosList.get(0).get(0).distance(myBasePos) > 10000) {
                myHeroes.add(new OffenseHero(id, x, y, shieldLife, isControlled, health, nearBase));
            }
            else  {
                myHeroes.add(new DefenseHero(id, x, y, shieldLife, isControlled, health, nearBase));
            }
        }

        public void update(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            myHeroes.stream()
                    .filter(hero -> hero.id == id)
                    .findFirst()
                    .ifPresent(hero -> hero.update(x, y, shieldLife, isControlled, health, nearBase));
        }

        public void onStepStart() {
            Hero.isWindCast = false;
            if (!didEnemyControlMe && this.myHeroes.stream().anyMatch(hero -> hero.isControlled)) {
                didEnemyControlMe = true;
            }
            myHeroes.forEach(hero -> hero.onStepStart());
        }

        public void assignTarget() {
            Monster newTarget = monsterWrap.monsters.stream()
                    .filter(monster -> monster.threatTo == ThreatTo.ME && !monster.isTargeted())
                    .min(Comparator.comparing(monster -> monster.pos.distance(myBasePos)))
                    .orElse(null);
            if (newTarget == null) {
                return;
            }

            System.err.println("\nNEW TARGET");
            System.err.println("===========");
            System.err.println(newTarget.toString());

            myHeroes.stream()
                    .filter(hero -> hero.targetId == -1)
                    .filter(hero -> hero.pos.distance(newTarget.pos) <= Hero.VISION_RANGE || hero instanceof DefenseHero)
                    .min(Comparator.comparing(hero -> hero.pos.distance(newTarget.pos)))
                    .ifPresent(hero -> hero.targetId = newTarget.id);
        }

        public void onStepEnd() {
            this.myHeroes.forEach(hero -> hero.onStepEnd());
        }

        public void print() {
            System.err.println("\nMY HEROES");
            System.err.println("=========");
            this.myHeroes.forEach(hero -> System.err.println(hero.toString()));
        }

        public Hero getControlledHero(Point pos, int range) {
            return this.myHeroes.stream()
                    .filter(hero -> hero.isControlled && hero.shields == 0)
                    .filter(hero -> hero.pos.distance(pos) <= range)
                    .findFirst()
                    .orElse(null);
        }
    }

    public static class EnemyHeroWrap {
        public List<EnemyHero> enemyHeroes = new ArrayList<>();

        public boolean contains(int heroId) {
            return enemyHeroes.stream().anyMatch(hero -> hero.id == heroId);
        }

        public void add(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            enemyHeroes.add(new EnemyHero(id, x, y, shieldLife, isControlled, health, nearBase));
        }

        public void print() {
            System.err.println("\nENEMY HEROES");
            System.err.println("============");
            enemyHeroes.forEach(hero -> System.err.println(hero.toString()));
        }

        public EnemyHero getDeepestEnemyHero(Point pos, int range) {
            return enemyHeroes.stream()
                    .filter(enemyHero -> enemyHero.shields == 0)
                    .filter(enemyHero -> enemyHero.pos.distance(enemyBasePos) < Base.ZONE_RANGE)
                    .filter(enemyHero -> enemyHero.pos.distance(pos) < range)
                    .min(Comparator.comparing(enemyHero -> enemyHero.pos.distance(enemyBasePos)))
                    .orElse(null);
        }

        public boolean isAnyNearby(Point pos, int range) {
            return enemyHeroes.stream().anyMatch(enemyHero -> enemyHero.pos.distance(pos) <= range);
        }
    }

    public static class MonsterWrap {
        public List<Monster> monsters = new ArrayList<>();

        public boolean contains(int id) {
            return monsters.stream().anyMatch(monster -> monster.id == id);
        }

        public void add(int id, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            monsters.add(new Monster(id, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor));
        }

        public Monster get(int id) {
            return monsters.stream()
                    .filter(monster -> monster.id == id)
                    .findFirst()
                    .orElse(null);
        }

        public Monster closestTo(Point idlePos) {
            return closestTo(idlePos, 999999);
        }

        public Monster closestTo(Point idlePos, int maxDistance) {
            return closestTo(idlePos, maxDistance, monster -> true);
        }

        public Monster closestTo(Point idlePos, int maxDistance, Predicate<Monster> filter) {
            return monsters.stream()
                    .filter(monster -> monster.pos.distance(idlePos) < maxDistance)
                    .filter(filter)
                    .min(Comparator.comparing(monster -> monster.pos.distance(idlePos)))
                    .orElse(null);
        }

        public Monster getShieldTargets(Point myHeroPos, int maxRangeToEnemyBase) {
            return monsters.stream()
                    .filter(monster -> monster.shields == 0)
                    .filter(monster -> monster.hp >= 16)
                    .filter(monster -> monster.threatTo == ThreatTo.ENEMY)
                    .filter(monster -> monster.pos.distance(myHeroPos) < Hero.SHIELD_RANGE)
                    .filter(monster -> monster.pos.distance(enemyBasePos) < maxRangeToEnemyBase)
                    .min(Comparator.comparing(monster -> monster.pos.distance(enemyBasePos)))
                    .orElse(null);

        }

        public int numAttackTargetsNearby(Point pos, int range) {
            return (int)monsters.stream()
                    .filter(monster -> monster.threatTo != ThreatTo.ENEMY)
                    .filter(monster -> pos.distance(monster.pos) <= range)
                    .count();
        }

        public int numWindTargets(Point heroPos) {
            Point windAddition = heroPos.unitVector(enemyBasePos).multiply(Hero.WIND_DISTANCE);
            return (int)monsters.stream()
                    .filter(monster -> monster.shields == 0)
                    .filter(monster -> monster.pos.distance(heroPos) < Hero.WIND_RANGE)
                    .filter(monster -> monster.pos.add(windAddition).distance(enemyBasePos) < Base.ZONE_RANGE + 1000)
                    .count();
        }

        public int numWindToEnemyZone(Point heroPos) {
            Point windAddition = heroPos.unitVector(enemyBasePos).multiply(Hero.WIND_DISTANCE);
            return (int)monsters.stream()
                    .filter(monster -> monster.shields == 0)
                    .filter(monster -> monster.threatTo != ThreatTo.ENEMY && !monster.isNearBase)
                    .filter(monster -> monster.pos.add(windAddition).distance(enemyBasePos) < Base.ZONE_RANGE)
                    .count();
        }

        public int numMonstersNearby(Point pos, int range) {
            return (int)monsters.stream()
                    .filter(monster -> monster.pos.distance(pos) <= range)
                    .count();
        }

        public Monster getScoringMonster() {
            return monsters.stream()
                    .filter(monster -> monster.threatTo == ThreatTo.ME && monster.isInMyZone())
                    .min(Comparator.comparing(monster -> monster.pos.distance(myBasePos)))
                    .orElse(null);
        }
    }

    public static class Base {
        public static int SCORE_RANGE = 299;
        public static int ZONE_RANGE = 4999;
        int mana;
        int lives;
        Point pos;
        Point corner1;
        Point corner2;
        Point centerPos;

        public Base(int lives, int mana, Point pos) {
            this.mana = mana;
            this.lives = lives;
            this.pos = pos;
            setPositions();
        }

        private void setPositions() {
            if (pos.x < 5000) { //top-left base
                corner1 = pos.add(4800, 100);
                corner2 = pos.add(100, 4700);
                centerPos = pos.add(Hero.VISION_RANGE, Hero.VISION_RANGE);
            }
            else {
                corner1 = pos.subtract(4800, 100);
                corner2 = pos.subtract(100, 4700);
                centerPos = pos.subtract(Hero.VISION_RANGE, Hero.VISION_RANGE);
            }
        }

        public Point pickTargetCorner(Point pos) {
            return pos.distance(corner1) + 2750 < pos.distance(corner2) ? corner1 : corner2;
        }

        @Override
        public String toString() {
            return "Base\nlives = " + lives +
                    ", mana = " + mana +
                    ", pos " + pos + "\n";
        }

        public static Point getCenterBasePos(Point basePos) {
            if (basePos.x < 5000) { //top-left base
                return basePos.add(Hero.VISION_RANGE, Hero.VISION_RANGE);
            }
            else {
                return basePos.subtract(Hero.VISION_RANGE, Hero.VISION_RANGE);
            }
        }
    }
}