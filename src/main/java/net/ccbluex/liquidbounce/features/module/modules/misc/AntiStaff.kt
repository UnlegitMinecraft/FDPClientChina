package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S1DPacketEntityEffect

@ModuleInfo(name = "AntiStaff", category = ModuleCategory.MISC)
class AntiStaff : Module() {

    val server  = ListValue("Server", arrayOf("BlocksMC","Jartex","Pika","Minebox","Universocraft","Zonecraft","Minelatino","Librecraft"),"BlocksMC")
    val chat = BoolValue("SendChatMessage",false)
    val message = TextValue("Message", "%staff% was detected as a staff member!").displayable { chat.get() }

    val leave = BoolValue("Leave",true)
    val leaveMessage = TextValue("LeaveCommand","/hub").displayable { leave.get() }

    private var bmcStaff : String = " iDhoom Jinaaan Eissaa Ev2n 1Mhmmd mohmad_q8 1Daykel Aliiyah 1Brhom xImTaiG_ comsterr 8layh M7mmd 1LaB xIBerryPlayz iiRaivy Refolt 1Sweet Aba5z3l EyesO_Diamond bestleso Firas reallyisntfair e9_ MK_F16 unrelievable Ixfaris_0 LuvDark 420kinaka _NonameIsHere_ iS3od_ 3Mmr Wesccar 1MeKo losingtears KaaReeeM loovq rarticalss 1RealFadi JustDrink_ AFG_progamer92 Jxicide D7oMz 1AhMqD Omaaaaaaaaaar Classic190 Only7oDa sylx69 1_3bdalH frank124 dfdox 1Mohq 1Sweleh_ Om2r epicmines33 1Devesty_ BagmaTwT Azyyq A2boD Ba1z 100k__ Watchdog nv0ola KinderBueno__ Invxe_ GreatMjd zixgamer Salvctore 420Lalilala vIon3 wstre AstroSaif plaintiveness ImS3G 1Flick EstieMeow ItsNqf MVP11 Daddy_Naif shichirouu Lordui 1Reyleigh BIocksMc 1Retired O_lp L6mh 63myh 1Mawja_ Tqfi 3iDO 1M7mmd__ lqmr yzed GsOMAR nshme Fcris RamboKinq qDry1 1Rana 1flyn Harbi deficency 0Aix 0Da3s 0DrRep 0hPqnos 0h_Roby 1A7mad1 1Ahmvd 1Derex 1DeVilz 1F5aMH___3oo 1HeyImHasson_ 1KhaleeD 1Kweng 1L7NN 1Levaai 1Loga_ 1LoST_ 1M0ha 1M7mdz 1M7mmD 1Mshari 1Narwhql 1Omxr 1Pepe_ 1RE3 1Sinqx 1Tz3bo 1_aq 1_ST 3rodi 7MZH 7re2a_YT 8mhh 90fa 91l7 9we A5oShnBaT abd0_369 Aboal3z14 Aboz3bl AbuA7md506 AfootDiamond117 AhmedPROGG Alaam_FG arbawii AsgardOfEddard AwKTaM Aymann_ baderr Banderr BaSiL_123 Bastic beyondviolets BinDontCare BlackOurs Blood_Artz Bo3aShor Bo6lalll bota_69 Boviix c22l Creegam Cryslinq CutieRana cuz_himo cW6n d5qq DaBabyFan DangPavel DarkA5_ deathally Dedz1k DeeRx DestroyerOnyc_ DestroyerTrUnKs Dizibre Dqrkfall Draggn_ Driction Du7ym ebararh EVanDoskI F2rris FaRidok FexoraNEP Flineer Fta7 Futurezii G3rryx GoldenGapples H2ris Haifa_magic HM___ iA11 iAhmedGG IDoubIe IF3MH iiEsaTKing_ iikimo iLuvSG_ ilybb0 iMehdi_ ImXann INFAMOUSEEE InjjectoR inVertice IR3DX iRxv iSolom Its_HighNoon Ittekimasu itzZa1D ixBander IxDjole IxKimo i_Ym5 Jarxay Jrx7 Just7MO KingHOYT KoFTH kostasidk Kuhimitsu lacvna lareey leeleeeleeeleee Lemfs lt1x Lunching Luvaa lwra M1M_ M4rwaan Maarcii manuelmaster Mark_Gamer_YT_ MaybeHeDoes Mhmovd MightyM7MD Millsap MindOfNasser Mjdra_call_ME mokgii Mondoros Mythiques mzh Neeres NotMoHqMeD__ obaida123445 ogm OldAlone Oxenaa phxnomenal PT7 qB6o6 qlxc qMabel qPito Raceth RADVN RealWayne real__happy redcriper Requieem ritclaw rixw1 rqnkk s2lm S3rvox saad6 Saajed Sadlly SalemBayern_ SamoXS sh5boo6 Sp0tzy_ SpecialAdam_ SpecialAdel_ STEEEEEVEEEE Tabby_Bhau Tetdeus TheDaddyJames TheDrag_Xx Thenvra TheOnlyM7MAD Tibbz_BGamer Tibbz_BGamer_ ToFy_ Tostiebramkaas ttkshr_ tverdy uh8e vBursT_ vdhvm vinnythebot vM6r vxom w7r wishingdeath wl3d wzii xanaxjuice xDiaa_levo xDmg xDupzHell xiDayzer xImMuntadher_ xIMonster_Rj xL2d xLePerfect xMz7 Y2men Yaazzeed yff3 yosife_7Y yQuack Y_04 zAhmd ZANAD zayedk zCroDanger Zqvies _0bX _b_i _iSkyla _N3 _Ottawa _R3 _SpecialSA_ _Vxpe _xayu_ _z2_"
    private var jartexStaff : String = "voodootje0 Max Rodagave Wrath JustThiemo Andeh Nirahz stupxd Botervrij Viclyn_  DrogonMC ovq Flexier NotLoLo1818 SabitTSDM07 ItzCqldFxld Laux  bene_e  iFlyYT HeadsBreker AX79 Technostein Djim Serpentsalamce Almostlikeaboss JustAtaman ZoneRGH naranbaatr louiekeys Difficulted FuzniX xHasey sammyxt CR7811149 Xerrainrin toastt_x UpperGround Swervinng SquareWings928 Yanique1 pakitonia Stxrs".toString()
    private var pikaStaff : String = "Max voodootje0 MrFrenco JustThiemo Wrath Andeh Nirahz stupxd Botervrij Subvalent Apo2xd Arrly Minecraft_leg CaptainGeoGR Thijme01 ChickenDinnr Crni_ MrGownz Outscale MrEpiko Crveni_Marlboro zMqrcc _Stella_xD Stormidity TryToHitMe Alparo_ CandyOP Astrospeh TinCanL TheTrueNova FIKOZ DarkVenom7 caila5 Lpkfvip i9BAR "
    private var mineboxStaff : String = "xSp3ctro_  SaF3rC  Sagui  TheSuperXD_YT  xAnibal  xTheKillex25x  HankWalter  JavierFenix  inothayami  ChaosSoleil  ElChamo300  Robert TO1010  itachi_uchiha_s  roku__  rynne_ sushi dashi Vicky_21"
    private var hycraftStaff : String = "Alexander245 arqui Blandih Chony_15 jac0mc Ragen06 TheBryaan TMT_131 Yapecito MartynaGamer830 archeriam"
    private var librecraftStaff : String = "Kudos  H0DKIER  Iker_XD9  acreate  iJeanSC  acreate  Janet  Rosse_RM  aldoum23neko_  DERGO  MJKINGPAND"
    private var universocraftStaff : String = "0edx_ 0_Lily 1Kao denila  fxrchus  haaaaaaaaaaax_ iBlackSoulz iMxon_ JuliCarles kvvwro Tauchet wSilv6r _JuPo_"
    private var minelatinoStaff : String = "zycragames _Dryout BunnyTruckSo amaelarco Norstonta Unexpected 1x12 Frachiza BabasTheBoring dhoslamarstack Bartolo_MC"
    private var detected = false
    private var staffs = "hypixel staff r loserz"
    
    
    @EventTarget
    fun onWorld(e: WorldEvent) {
        when (server.get().lowercase()) {
            "blocksmc" -> {
                staffs = bmcStaff
            }
            
            "jartex" -> {
                staffs = jartexStaff
            }
            
            "pika" -> {
                staffs = pikaStaff
            }
            
            "minebox" -> {
                staffs = mineboxStaff
            }

            "universocraft" -> {
                staffs = universocraftStaff
            }
            
            "minelatino" -> {
                staffs = minelatinoStaff
            }
            
            "hycraft" -> {
                staffs = hycraftStaff
            }
            
            "librecraft" -> {
                staffs = librecraftStaff
            }

            
        }
            
        detected = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (mc.theWorld == null || mc.thePlayer == null) return

        val packet = event.packet // smart convert
        if (packet is S1DPacketEntityEffect) {
            val entity = mc.theWorld.getEntityByID(packet.entityId)
            if (entity != null && (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    LiquidBounce.hud.addNotification(Notification(name, "Detected staff members with invis. You should quit ASAP.", NotifyType.WARNING, 8000))
                    
                    if (chat.get()) {
                        ClientUtils.displayChatMessage((message.get()).replace("%staff%", entity.name))
                    }
                    if (leave.get()) {
                        mc.thePlayer.sendChatMessage(leaveMessage.get())
                    }
                    
                    detected = true
                }
            }
        }
        if (packet is S14PacketEntity) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    LiquidBounce.hud.addNotification(Notification(name, "Detected staff members. You should quit ASAP.", NotifyType.WARNING,8000))
                    
                    if (chat.get()) {
                        ClientUtils.displayChatMessage((message.get()).replace("%staff%", entity.name))
                    }
                    
                    if (leave.get()) {
                        mc.thePlayer.sendChatMessage(leaveMessage.get())
                    }
                    
                    detected = true
                }
            }
        }
    }
}
