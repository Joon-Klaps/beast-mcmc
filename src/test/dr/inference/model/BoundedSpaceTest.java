package test.dr.inference.model;


import dr.inference.model.BoundedSpace;
import dr.inference.operators.hmc.HamiltonianMonteCarloOperator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BoundedSpaceTest {

    private class Instance {
        public final double[] position;
        public final double[] velocity;
        public final boolean isAtBoundary;
        public final double actual;

        Instance(double[] position, double[] velocity, boolean isAtBoundary, double actual) {
            this.position = position;
            this.velocity = velocity;
            this.isAtBoundary = isAtBoundary;
            this.actual = actual;
        }
    }

    Instance[] instances = new Instance[]{
            new Instance( // dense velocity, not at boundary
                    new double[]{0.2822855517829705, 0.40605243926469287, 0.02849762638750524, -0.564844016766597, 0.10245003047216325, 0.0965515090688095, 0.36620919740224617, -0.01787268001498262, 0.6166518433752171, 0.33255922064276344, 0.3486639561922815, 0.20009002909341947, 0.03369031306626756, 0.3913488688811744, 0.6333813479719003, -0.6524997126334955, -0.23103935804969739, 0.5721530659041922, -0.09121186775518811, -0.33094967030159816, 0.10373856188477253, 0.2358517748200947, 0.28136454978838454, 0.38180511299157194, 0.03857632199153963, -0.10247319649770201, -0.4515249611583288, 0.04069735163371401, 0.2739134475778366, 0.20394511869828952,
                            0.16987513814725316, 0.0498047833476856, -0.23155751470047312, -0.34084092179183206, -0.5435689844314044, -0.22997407806145242, -0.524907999369761, -0.26982552685916034, 0.013582732323656897, 0.6283381537641279, -0.5262437447150251, -0.24568348204054502, -0.48506559078208455, -0.006540034446863485, 0.487435821074331},
                    new double[]{2.0438614777153097, -0.7395282076530152, -0.04671949232593737, -0.05568195314160399,
                            -1.7503124002123156, 1.438912952418718, -1.2979060424575182, -0.12967355482400317, -0.5877605903799071, 0.4663973722472238, 0.6501194485236997, -0.27427888048772114, 0.7773041621717932, -1.2529452491475837, 0.1264625176370857, -2.184869901041784, -2.3210809198970628, 0.4536691205497068, 0.0040154775744028525, -0.02022797360190504, -0.4122624930974949, -0.8144835256337633, 1.4565197109479235, 1.240849853686729, 0.985253115077047, 0.4962656279813027, -0.21246925243198708, -0.5021739010419594, 1.4565629697944975, -1.1261205391371558, 0.8147966861488231, -0.08483633566338881, -0.7918752798447827, 0.15361236989858948, 0.14789511915893988, -1.6506804194613995, 0.20917813332349894, -1.020547878554704, 0.6933439963746123, -0.8804450733870868, -1.0296658859958183, 0.2777824369980236, 0.429102310041008, -0.1696873347531642, 0.43901426447036324},
                    false,
                    0.01595563335319449),
            new Instance( // dense velocity, at boundary
                    new double[]{0.11647159249911301, -0.08506425456560686, 0.06052011411111528, 0.6280642539160219, 0.1858869704724907, 0.2707344946140536, -0.20593108085359318, -0.23371769475449147, -0.3535532648113636, 0.2584924019161337, 0.49145781749224526, -0.08545927119819614, 0.0019223081229396148, -0.17576719543866787, 0.5669613122986198, -0.3811300793200186, 0.048178306235236744, -0.24174056602815053, 0.10075016419804625, -0.04283060683464314, 0.24322613111387503, 0.5120677150420093, -0.026830637068174238, 0.004388348867356102, 0.021323360340519286, 0.0675870251067074, -0.673678610283607, 0.20730194234977975, -0.25512501925923997, 0.6381849075861147, -0.07200433671028439, 0.1623547683774016, -0.27416192799573713, -0.10673348908034844, -0.12657024780739462, -0.2944885708858614, -0.22545327796218337, -0.3349602644633396, -0.12418255126124608, -0.0781009754221288, 0.00985739557424605, -0.7906189231671669, 0.042233590443951856, 0.3912512164379495, 0.27717395861287647},
                    new double[]{-1.0346921156530475, -0.7834466818276314, -0.6036925610549285, -1.6488512182861115, -0.8023765324230373, -0.36518794172088387, -0.5445949672312552, 0.17745174291760565, 0.41790594874801706, 0.09060918246766107, 0.354260412636501, 1.9062401157318938, 1.140465086875608, -0.13439202235311207, 1.2385557416800101, -0.08380905464237058, 0.599994532149525, -0.6963958583478322, 0.23334316441818698, -1.052992644030231, -0.2593191511965254, 1.3702363112988352, -0.06139381721406518, -1.4380644122199022, -1.7585887877096682, 1.2412102713846938, 0.4758368659296449, -1.3717994142343595, 1.43323783535285, -2.091234335971886, 0.9082181607882182, 0.08267774926196575, -1.3011183073338028, 0.9517052987954928, -2.7855629538249462, -1.5071558646552672, 1.6031446100634232, -0.23005292084728482, 0.5750574858677685, -0.09568198613982926, 0.16991262670991295, -1.194864379072432, 0.24365560223571764, -0.343107465013622, -0.7663578593237961},
                    true,
                    0.01223608297387474),
            new Instance( // sparse velocity, not at boundary
                    new double[]{0.0670476094576967, 0.4367961067430731, 0.48664883054844366, -0.19274573625080862, -0.2744063095062948, -0.15306145989259248, 0.30721493611428496, 0.16776289055002544, 0.08104118685651174, -0.6227814065271465, 0.0731979822708254, 0.7857019348678728, 0.2677449731175475, -0.5502088508980884, 0.03771846114331344, 0.3190168387554593, -0.22047212328810817, -0.06540149474172938, -0.687608567476592, -0.34528076191263624, 0.22370110435756238, 0.040256367672321235, -0.22740372706234033, 0.2366142305038848, -0.03725095935153343, 0.2510891496166299, -0.03355890231101856, -0.007285698073503861, 0.6427633993305558, -0.21383544251461112, -0.0016195571510596834, -0.46644562664579325, -0.11856508641792099, 0.29565621479439363, -0.30720691413587015, 0.15803683808011923, 0.2828987312846409, 0.4414784551297542, -0.5221583157958694, 0.40676508546537504, -0.4944084288272014, 0.24916361029681994, -0.2094230343411282, -0.0976994550220947, -0.6795147439292425},
                    new double[]{0.0, 0.0, -1.0683641929041463, 1.4074769699960024, 2.4076197351280726, -0.7071752529563077, 0.0902219088734521, -1.1701540514148658, -0.8850141813046155, 0.0, 0.43200829972893906, -0.7761314710437377, 0.7440087608644848, -0.024508316794491402, -1.423953745919625, -0.3052756083657273, 1.1379728532070383, 1.812377176684857, -1.5317894306075623, -0.009775591333715548, 1.1282507432924553, 0.7115553368277749, 1.5015660134490751, -0.5569539859571256, 0.0, 0.0, 0.0, 0.0, 0.0, -1.4341355880302382, 0.0, 0.0, 0.0, 0.0, 0.6427033706412396,
                            0.0, 0.0, 0.0, -0.5146126961480285, 0.0, 0.0, -0.6411849536801717, 0.0, 0.38494833959549835, -0.4133283160935234},
                    false,
                    0.024629529545630423),
            new Instance( // sparse velocity, at boundary
                    new double[]{-0.26757589087078193, -0.02938569555892525, 0.18513857054513194, 0.45426488679410915, -0.01763137023312626, -0.12490569292229133, -0.02248584573483657, 0.3105550081285866, 0.22388294813341902, -0.04819405233443179, 0.31075193194590905, 0.4883275878373804, 0.48203706437713173, -0.10747538571923325, 0.08967928867045576, -0.3804060916333354, -0.22723677965430755, -0.05680381914344709, -0.2714994265633495,
                            0.6064673183295967, 0.11161324462019362, 0.02432671422275462, 0.3439120417020678, -0.028657224962422713, 0.2664948230582488, 0.40488926421869764, -0.27352248738407703, 0.6467077532653467, 0.40021128203106154, 0.5023778368436919, 0.318781718004652, -0.20214876545585567, -0.32225743555541214, -0.19859068384300757, -0.18995352580103042, 0.11905713166260283, 0.0011462916709671846, 0.09599237129692728, -0.1260666035857841, -0.14407227967969546, 0.09281799565543995, -0.21075798426389972, 0.16880530157106863, 0.38074163001542666, 0.7018785056040518},
                    new double[]{-0.0, -0.0, -0.8822912283623365, -0.07850848303667071, 1.7168396136971413, 0.33187232269944494, -0.12134146134082555, -0.35206153660614065, 0.884644612485139, -0.0, -1.9123537150968595, -0.5569287897049415, 0.354481275240167, -1.1684131108537485, 0.4189200083984581, 0.5675955066325348, 1.446092480296222, -0.15266121755447873, -0.32814205291827453, -0.3222217609135461, -0.7644477043670591, -1.9493869595983575, -0.616075655009724, -1.374788715704303, -0.0, -0.0, -0.0, -0.0, -0.0, -1.1129531496787755, -0.0, -0.0, -0.0, -0.0, 0.6765725834261671, -0.0, -0.0, -0.0, 1.2525347271504157, -0.0, -0.0, -0.7057402390019548, -0.0, -0.40903841628897586, -1.2628021481812326},
                    true,
                    0.014146061076949323)
    };


    @Test
    public void CorrelationTest() {
        int ind = 0;
        for (Instance instance : instances) {
            ind++;
            int dim = (1 + (int) Math.round(Math.sqrt(1 + 8 * instance.position.length))) / 2;
            BoundedSpace.Correlation correlationBound = new BoundedSpace.Correlation(dim);

            double t;
            try {
                t = correlationBound.forwardDistanceToBoundary(instance.position, instance.velocity, instance.isAtBoundary);
            } catch (HamiltonianMonteCarloOperator.NumericInstabilityException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            assertEquals("correlation matrix " + ind, instance.actual, t, 1e-10);

        }

    }
}
