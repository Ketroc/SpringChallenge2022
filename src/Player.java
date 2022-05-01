import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public static boolean isEnemyTripleWind;

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
            monsterWrap.updateFoggedMonsters();
            setMonsterHp();
            enemyTripleWindToggle();
            System.err.println("isEnemyTripleWind = " + isEnemyTripleWind);
//            System.err.println("MY BASE");
//            System.err.println("=======");
//            System.err.println(myBase);
//            System.err.println("ENEMY BASE");
//            System.err.println("==========");
//            System.err.println(enemyBase);

            monsterWrap.print();

            System.err.println("===> prior to onstepstart");
            //heroWrap.print();
            heroWrap.onStepStart();

            System.err.println("===> prior to assign target");
            //heroWrap.print();
            heroWrap.assignTarget();

            System.err.println("===> prior to onstepend");
            //heroWrap.print();
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

    private static void enemyTripleWindToggle() {
        isEnemyTripleWind = false;
//        if (enemyHeroWrap.enemyHeroes.stream().anyMatch(enemyHero -> enemyHero.pos.distance(myBase.pos) > 12500)) {
//            isEnemyTripleWind = false;
//        }
//        else if (enemyHeroWrap.enemyHeroes.size() == 0) {
//            isEnemyTripleWind = false;
//        }
//        else if (enemyHeroWrap.enemyHeroes.size() == 3) {
//            isEnemyTripleWind = enemyHeroWrap.enemyHeroes.stream().allMatch(enemyHero -> enemyHero.pos.distance(myBase.pos) < 11000);
//        }
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
//            middle.add(enemyBaseCenterPos);
//            middle.add(new Point(13000, 2500));
//            middle.add(new Point(9500, 6500));
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
//            middle.add(enemyBaseCenterPos);
//            middle.add(new Point(5000, 6500));
//            middle.add(new Point(8000, 2000));
//            middle.add(enemyBaseCenterPos);
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
            return isNearBase && myBase.zoneContains(pos);
        }

        public boolean isInEnemyZone() {
            return isNearBase && enemyBase.zoneContains(pos);
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
        int prevVisibleStep;
        int prevMyControlStep;
        boolean removeMe;

        public Monster(int id, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
            setThreatTo(threatFor);
            this.vector = new Point(vx, vy);
            this.prevVisibleStep = step;
        }

        public void update(int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            this.pos = new Point(x, y);
            this.shields = shieldLife;
            this.isControlled = isControlled == 1;
            this.hp = health;
            if (step >= prevMyControlStep + 2) {
                this.vector = new Point(vx, vy);
            }
            this.isNearBase = nearBase == 1;
            setThreatTo(threatFor);
            this.prevVisibleStep = step;
        }

        public void update() {
            pos = pos.add(vector);
            if (!pos.isInBounds() ||
                    pos.isVisible() ||
                    enemyBase.pos.distance(pos) < Base.SCORE_RANGE) {
                removeMe = true;
            }
            if (enemyBase.zoneContains(pos)) {
                pos = pos.inBounds();
                isNearBase = true;
                threatTo = ThreatTo.ENEMY;
                vector = pos.getVector(enemyBase.pos, Monster.SPEED);
            }
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

        public void setControlled(Point controlToPos) {
            prevMyControlStep = step;
            vector = pos.add(vector).getVector(controlToPos, Monster.SPEED);
            isControlled = true;
            threatTo = ThreatTo.ENEMY;
        }

        private int numStepsToScoreOnMe() {
            if (!isInMyZone()) {
                return -1;
            }
            Point posStep = pos;
            int numSteps = 0;
            while (posStep.distance(myBase.pos) > Base.SCORE_RANGE) {
                posStep = posStep.add(vector);
                numSteps++;
                if (numSteps > 20) {
                    return -1;
                }
            }
            return numSteps;
        }

        private int numStepsToScoreOnEnemy() {
            if (!isInEnemyZone()) {
                return -1;
            }
            Point posStep = pos;
            int numSteps = 0;
            while (posStep.distance(enemyBase.pos) > Base.SCORE_RANGE) {
                posStep = posStep.add(vector);
                numSteps++;
                if (numSteps > 20) {
                    return -1;
                }
            }
            return numSteps;
        }

        protected Point nextPos() {
            return pos.add(vector);
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
        static int ATTACK_RANGE = 800;
        static int CONTROL_RANGE = 2199;
        static int SHIELD_RANGE = 2199;
        static int VISION_RANGE = 2199;
        static int WIND_RANGE = 1279;
        static int WIND_DISTANCE = 2199;

        static boolean isWindCast;

        Point idlePos;
        int targetId = -1;
        List<Point> patrolPoints;
        List<Point> patrolGoalLine = Arrays.asList(myBase.pos, myBase.pos);
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

        public List<Point> getPatrolPoints() {
            if (!isEnemyTripleWind) {
                return patrolPoints;
            }
            return patrolGoalLine;
        }

        public Point getInterceptPos(Monster targetMonster) {
            if (targetMonster.nextPos().distance(pos) < Hero.SPEED) {
                return calcBestAttackPos(pos, targetMonster.nextPos());
            }

            Point interceptPos = targetMonster.pos;
            Point prevInterceptPos = interceptPos;
            while (interceptPos.isInBounds() &&
                    pos.distance(interceptPos) / 2 > targetMonster.pos.distance(interceptPos)) {
                prevInterceptPos = interceptPos;
                interceptPos = interceptPos.add(targetMonster.vector);
            }
            return prevInterceptPos;
        }

        public void antiTripleWind() {
//            //SHIELD up when getting in range
//            if (shields == 0 && enemyHeroes.stream().anyMatch(enemyHero -> enemyHero.pos.distance(pos) < Hero.WIND_DISTANCE + Hero.SPEED)) {
//                shield(id, "3x WIND");
//            }

            //WIND the heroes
            if (step > prevWindFrame + 1 &&
                    enemyHeroWrap.enemyHeroes.stream()
                            .anyMatch(enemyHero -> enemyHero.shields == 0 &&
                                    enemyHero.pos.distance(pos) < (WIND_RANGE - Hero.SPEED - 2))) {
                wind(pos.towards(myBase.pos, -WIND_DISTANCE), "D WIND");
            }

            //SHIELD up partner
            EnemyHero leadEnemyHero = enemyHeroWrap.getClosest(myBase.pos);
            Point leadEnemyHeroPos = leadEnemyHero.pos.towards(myBase.pos, 400);
            Hero heroToShield = heroWrap.myHeroes.stream()
                    .filter(hero -> hero.shields == 0 &&
                            hero.pos.distance(pos) < Hero.SHIELD_RANGE &&
                            hero.pos.distance(leadEnemyHeroPos) < pos.distance(leadEnemyHeroPos))
                    .min(Comparator.comparing(hero -> hero.pos.distance(leadEnemyHeroPos)))
                    .orElse(null);
            if (heroToShield != null) {
                shield(id, "D PARTNER");
            }

            //MOVE towards enemy heroes
            move(leadEnemyHeroPos, leadEnemyHeroPos.toString());
        }

        private Point calcBestAttackPos(Point heroPos, Point targetPos) {
            List<Monster> monstersInRange = monsterWrap.monsters.stream()
                    .filter(monster -> monster.pos.distance(heroPos) < Hero.SPEED + Hero.ATTACK_RANGE + 10)
                    .filter(monster -> monster.pos.distance(targetPos) < Hero.ATTACK_RANGE * 2)
                    .collect(Collectors.toList());

            List<Point> samplePosList = pos.getTestPositions(Hero.ATTACK_RANGE);
            return samplePosList.stream()
                    .filter(samplePos -> samplePos.isInBounds())
                    .filter(samplePos -> samplePos.distance(heroPos) < Hero.SPEED)
                    .max(Comparator.comparing(samplePos ->
                            monstersInRange.stream()
                                    .filter(monster -> monster.pos.distance(samplePos) < Hero.ATTACK_RANGE)
                                    .mapToInt(monster -> (monster.threatTo != ThreatTo.ENEMY ? 20000 : -10000) -
                                            (int)samplePos.distance(samplePosList.get(0)))
                                    .sum()))
                    .orElse(samplePosList.get(0));
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
            int numPatrolPoints = (monsterHp < 18) ? 2 : getPatrolPoints().size();
            curPatrolIndex = (curPatrolIndex + 1) % numPatrolPoints;
            idlePos = getPatrolPoints().get(curPatrolIndex);
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

        //17hp = 135mana, 19hp = 85mana, 22hp = 10mana
        public boolean isControlWorthMana(int monsterHp) {
            return myBase.mana - 25 * (22 - monsterHp) >= 10;
        }

        public Monster getFarmingTarget() {
            return monsterWrap.closestTo(pos,
                    m -> m.threatTo != ThreatTo.ENEMY &&
                            getPatrolPoints().stream().anyMatch(patrolPos -> patrolPos.distance(m.nextPos()) < 3500));
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
            if (isEnemyTripleWind) {
                Point leadEnemyHero = enemyHeroWrap.getClosest(myBase.pos).pos;
                int closestHeroId = heroWrap.myHeroes.stream()
                        .filter(hero -> hero instanceof DefenseHero)
                        .min(Comparator.comparing(hero -> hero.pos.distance(leadEnemyHero)))
                        .get().id;
                if (id == closestHeroId) {
                    antiTripleWind();
                    return;
                }
                targetId = -1;
            }

            //SHIELD my partner if controlled
            if (myBase.mana >= 10) {
                Hero controlledHero = heroWrap.getControlledHero(pos, SHIELD_RANGE);
                if (controlledHero != null) {
                    shield(controlledHero.id);
                    return;
                }
            }

            //SHIELD self vs player who uses CONTROL
            if (didEnemyControlMe &&
                    shields == 0 &&
                    myBase.mana > 150 &&
                    pos.distance(myBasePos) < 8000 &&
                    enemyHeroWrap.isAnyNearby(pos, CONTROL_RANGE + SPEED)) {
                shield(id);
                return;
            }

            //assist emergency target
            if (targetId == -1) {
                Monster scoringMonster = monsterWrap.getScoringMonster();
                if (scoringMonster != null) {
                    double monsterDistance = scoringMonster.pos.distance(pos);
                    if (monsterDistance < 2500) {
                        targetId = scoringMonster.id;
                    }
                }
            }

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
                        targetMonster.nextPos().distance(myBasePos) >= 5000 &&
                        targetMonster.pos.distance(pos) <= CONTROL_RANGE &&
                        myBase.mana > 20 &&
                        targetMonster.hp > 15 &&
                        (monsterWrap.numAttackTargetsNearby(getIdlePos(), 4000) > 1 ||
                                monsterWrap.numAttackTargetsNearby(myBasePos, 5000) > 1)) {
                    Point controlToPos = targetMonster.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(targetMonster.pos) :
                            enemyBasePos;
                    control(targetId, controlToPos, targetMonster.vector.toString());
                    targetMonster.setControlled(controlToPos);
                    targetId = -1;
                    return;
                }

                //WIND monster
                if (!Hero.isWindCast &&
                        step > prevWindFrame + 1 &&
                        myBase.mana >= 10 &&
                        targetMonster.shields == 0 &&
                        targetMonster.threatTo == ThreatTo.ME &&
                        distanceToBase <= getDefensiveWindZone() &&
                        targetMonster.pos.distance(pos) <= WIND_RANGE) {
                    wind(enemyBasePos);
                    return;
                }

                //ATTACK monster
                move(getInterceptPos(targetMonster), "DEFEND");
                return;
            }

            //CONTROL neutral monster
            if (myBase.mana > 50) {
                Monster controlTarget  = monsterWrap.getControlTarget(this);
                if (controlTarget != null) {
                    Point controlToPos = controlTarget.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(controlTarget.pos) :
                            enemyBasePos;
                    control(controlTarget.id, controlToPos);
                    controlTarget.setControlled(controlToPos);
                    return;
                }
            }

            //attack nearest neutral monster
            Monster nearbyMonster = getFarmingTarget();
            if (nearbyMonster != null) {
                move(getInterceptPos(nearbyMonster), "FARM");
                return;
            }

            //go to idle position
            patrol();
        }

        private double getDefensiveWindZone() {
            //return Base.ZONE_RANGE;
            return (monsterHp >= 15 || enemyHeroWrap.isAnyNearby(myBase.pos, Base.ZONE_RANGE + 2000)) ?
                    Base.ZONE_RANGE :
                    Monster.SPEED + Base.SCORE_RANGE + 100;
        }

        @Override
        public String toString() {
            return "DefenseHero\ntargetId = " + targetId +
                    super.toString();
        }
    }

    public static class OffenseHero extends Hero {
        boolean enemyControlsMe;

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
            if (isEnemyTripleWind) {
                antiTripleWind();
                return;
            }

            if (isControlled && isFinisherMode()) {
                enemyControlsMe = true;
            }

            //SHIELD up when trying to score if opponent using CONTROL
            if (shields == 0 &&
                    myBase.mana >= 10 &&
                    isFinisherMode() &&
                    enemyHeroWrap.isAnyNearby(pos, Hero.CONTROL_RANGE)) {
                shield(id);
                return;
            }

            //Create 2+ new threats with WIND
            if (step > prevWindFrame + 1 &&
                    pos.distance(enemyBase.pos) < 10000 &&
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
            if (enemyBase.zoneContains(pos) &&
                    monsterWrap.numMonstersNearby(enemyBasePos, Base.ZONE_RANGE / 2) > 1) {
                EnemyHero enemyHero = enemyHeroWrap.getDeepestEnemyHero(pos, Hero.CONTROL_RANGE);
                if (enemyHero != null) {
                    control(enemyHero.id, pos.towards(enemyBasePos, -VISION_RANGE), "Dance, minion!");
                    return;
                }
            }

            //defending
            if (targetId != -1) {
                Monster targetMonster = monsterWrap.get(targetId);
                double distanceToBase = targetMonster.pos.distance(myBasePos);

                //CONTROL monster
                if (targetMonster.shields == 0 &&
                        distanceToBase >= 5000 &&
                        targetMonster.pos.distance(pos) <= CONTROL_RANGE &&
                        myBase.mana > 20 &&
                        targetMonster.hp > 13 &&
                        (monsterWrap.numAttackTargetsNearby(getIdlePos(), 4000) > 1 ||
                                monsterWrap.numAttackTargetsNearby(myBasePos, 5000) > 1)) {
                    Point controlToPos = targetMonster.pos.distance(enemyBasePos) > 7500 ?
                            enemyBase.pickTargetCorner(targetMonster.pos) :
                            enemyBasePos;
                    control(targetId, controlToPos, targetMonster.vector.toString());
                    targetMonster.setControlled(controlToPos);
                    targetId = -1;
                    return;
                }

                //ATTACK monster
                move(getInterceptPos(targetMonster), "DEFEND");
                return;
            }

            //farming
            if (!isFinisherMode()) {
                //CONTROL neutral monster
                if (myBase.mana > 50) {
                    Monster controlTarget = monsterWrap.getControlTarget(this);
                    if (controlTarget != null) {
                        Point controlToPos = controlTarget.pos.distance(enemyBasePos) > 7500 ?
                                enemyBase.pickTargetCorner(controlTarget.pos) :
                                enemyBasePos;
                        control(controlTarget.id, controlToPos);
                        controlTarget.setControlled(controlToPos);
                        return;
                    }
                }

                //attack nearest neutral monster
                Monster nearbyMonster = getFarmingTarget();
                if (nearbyMonster != null) {
                    move(getInterceptPos(nearbyMonster), "FARM");
                    return;
                }
            }

            //go to idle position
            patrol(isFinisherMode() ? "SCORE" : "PATROL");
        }

        public boolean isFinisherMode() {
            return step > 210 ||
                    (myBase.mana >= 20 && myBase.lives <= enemyBase.lives && step > 190) ||
                    (monsterWrap.numMonstersScorable() > 3);
        }

        @Override
        public String toString() {
            return "OffenseHero\n" + super.toString();
        }
    }

    public static class MapConstants {
        public static int MAP_WIDTH = 17630;
        public static int MAP_HEIGHT = 9000;

        public static Point BASE1 = new Point(0,0);
        public static Point BASE2 = new Point(MAP_WIDTH,MAP_HEIGHT);
        public static Point MONSTER_SPAWN1 = new Point(8750,0);
        public static Point MONSTER_SPAWN2 = new Point(4750, MAP_HEIGHT);
        public static Point MONSTER_SPAWN3 = new Point(12750,0);
        public static Point MONSTER_SPAWN4 = new Point(8915, MAP_HEIGHT);
    }

    public static class AntiTripleWindHero extends Hero {
        public AntiTripleWindHero(int id, int x, int y, int shieldLife, int isControlled, int health, int nearBase) {
            super(id, x, y, shieldLife, isControlled, health, nearBase);
        }

        @Override
        public void onStepStart() {

        }

        @Override
        public void onStepEnd() {
            antiTripleWind();
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

        public boolean isInBounds() {
            return x >= 0 && y >= 0 && x <= 17630 && y <= 9000;
        }

        public static Point normalize(Point vector) {
            double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
            return new Point((vector.x / length), (vector.y / length));
        }

        public Point getVector(Point targetPos, int speed) {
            return towards(targetPos, speed).subtract(this);
        }

        public boolean isVisible() {
            return distance(myBase.pos) < Base.VISION_RANGE ||
                    heroWrap.myHeroes.stream().anyMatch(hero -> hero.pos.distance(this) < Hero.VISION_RANGE);
        }

        public Point inBounds() {
            return new Point(
                    Math.min( Math.max(x, 0), 17630),
                    Math.min( Math.max(y, 0), 9000)
            );
        }

        public List<Point> getTestPositions(int range) {
            ArrayList<Point> posList = new ArrayList<>();
            posList.add(this);
            posList.add(this.add(0, range));
            posList.add(this.add(0, -range));
            posList.add(this.add(range, 0));
            posList.add(this.add(-range, 0));

            posList.add(this.add(0, range/2));
            posList.add(this.add(0, -range/2));
            posList.add(this.add(range/2, 0));
            posList.add(this.add(-range/2, 0));

            posList.add(this.towards(this.add(range, range), range-1));
            posList.add(this.towards(this.add(range, -range), range-1));
            posList.add(this.towards(this.add(-range, range), range-1));
            posList.add(this.towards(this.add(-range, -range), range-1));

            posList.add(this.towards(this.add(range, range), range/2));
            posList.add(this.towards(this.add(range, -range), range/2));
            posList.add(this.towards(this.add(-range, range), range/2));
            posList.add(this.towards(this.add(-range, -range), range/2));

            return posList;
        }

        public Point divide(int divideBy) {
            if (divideBy == 0) {
                return this;
            }
            return new Point((int)(x/divideBy), (int)(y/divideBy));
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
                    .filter(enemyHero -> enemyBase.zoneContains(enemyHero.pos))
                    .filter(enemyHero -> enemyHero.pos.distance(pos) < range)
                    .min(Comparator.comparing(enemyHero -> enemyHero.pos.distance(enemyBasePos)))
                    .orElse(null);
        }

        public boolean isAnyNearby(Point pos, int range) {
            return enemyHeroes.stream().anyMatch(enemyHero -> enemyHero.pos.distance(pos) <= range);
        }

        public List<EnemyHero> getNearby(Point pos, int range) {
            return getNearby(pos, range, enemyHero -> true);
        }

        public List<EnemyHero> getNearby(Point pos, int range, Predicate<EnemyHero> filter) {
            return enemyHeroes.stream()
                    .filter(enemyHero -> enemyHero.pos.distance(pos) <= range)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        public Point getEnemyHeroesMidPoint() {
            Point midPoint = new Point(0, 0);
            for (EnemyHero enemyHero : enemyHeroes) {
                midPoint = midPoint.add(enemyHero.pos);
            }
            return midPoint.divide(enemyHeroes.size());
        }

        public EnemyHero getClosest(Point pos) {
            return enemyHeroes.stream()
                    .min(Comparator.comparing(enemyHero -> enemyHero.pos.distance(pos)))
                    .orElse(null);
        }
    }

    public static class MonsterWrap {
        public List<Monster> monsters = new ArrayList<>();

        public boolean contains(int id) {
            return monsters.stream().anyMatch(monster -> monster.id == id);
        }

        public void add(int id, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            if (contains(id)) {
                update(id, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor);
            }
            else  {
                monsters.add(new Monster(id, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor));
            }
        }

        public void update(int id, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            monsters.stream()
                    .filter(monster -> monster.id == id)
                    .findFirst()
                    .ifPresent(monster -> monster.update(x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor));
        }

        public Monster get(int id) {
            return monsters.stream()
                    .filter(monster -> monster.id == id)
                    .findFirst()
                    .orElse(null);
        }

        public Monster closestTo(Point idlePos, Predicate<Monster> filter) {
            return closestTo(idlePos, 999999, filter);
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
                    .filter(monster -> monster.hp >= 18)
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

        public int numWindToEnemyZone(Point heroPos) {
            Point windVector = heroPos.unitVector(enemyBasePos).multiply(Hero.WIND_DISTANCE);
            return (int)monsters.stream()
                    .filter(monster -> monster.shields == 0)
                    .filter(monster -> monster.pos.distance(heroPos) < Hero.WIND_RANGE)
                    .filter(monster -> monster.threatTo != ThreatTo.ENEMY && !monster.isNearBase)
                    .filter(monster -> enemyBase.zoneContains(monster.pos.add(windVector)))
                    .count();
        }

        public int numMonstersNearby(Point pos, int range) {
            return (int)monsters.stream()
                    .filter(monster -> monster.pos.distance(pos) <= range)
                    .count();
        }

        public int numMonstersScorable() {
            return (int)monsters.stream()
                    .filter(monster -> monster.threatTo == ThreatTo.ENEMY)
                    .filter(monster -> monster.pos.distance(enemyBase.pos) <= Base.ZONE_RANGE + 1000)
                    .count();
        }

        public Monster getScoringMonster() {
            return monsters.stream()
                    .filter(monster -> monster.threatTo == ThreatTo.ME && monster.isInMyZone())
                    .min(Comparator.comparing(monster -> monster.pos.distance(myBasePos)))
                    .orElse(null);
        }

        public void print() {
            //monsters.forEach(monster -> System.out.println(monster));
            System.err.println("MONSTERS");
            System.err.println("========");
            monsters.forEach(monster -> System.err.println("id: " + monster.id +
                    " pos: " + monster.pos +
                    " vector: " + monster.vector +
                    " isControlled: " + monster.isControlled
            ));
        }

        public void updateFoggedMonsters() {
            monsters.stream()
                    .filter(monster -> monster.prevVisibleStep < step)
                    .forEach(monster -> monster.update());
            monsters.removeIf(monster -> monster.removeMe);
        }

        public Monster getControlTarget(Hero hero) {
            return monsters.stream()
                    .filter(monster -> monster.shields == 0 && monster.threatTo != ThreatTo.ENEMY)
                    .filter(monster -> hero.isControlWorthMana(monster.hp))
                    .filter(monster -> monster.pos.distance(hero.pos) <= Hero.CONTROL_RANGE)
                    .min(Comparator.comparing(monster -> monster.pos.distance(enemyBase.pos)))
                    .orElse(null);
        }
    }

    public static class Base {
        public static int SCORE_RANGE = 299;
        public static int ZONE_RANGE = 4999;
        public static int VISION_RANGE = 5999;
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
            return pos.distance(corner1) + 2800 < pos.distance(corner2) ? corner1 : corner2;
        }

        public boolean zoneContains(Point p) {
            return p.distance(pos) < Base.ZONE_RANGE;
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