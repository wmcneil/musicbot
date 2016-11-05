package fredboat.feature;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.event.AbstractScopedEventListener;
import fredboat.event.UserListener;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

public final class AkinatorListener extends UserListener {

    private final String NEW_SESSION_URL = "http://api-en4.akinator.com/ws/new_session?partner=1&player=desktopPlayer";
    private final String ANSWER_URL = "http://api-en4.akinator.com/ws/answer";
    private final String GET_GUESS_URL = "http://api-en4.akinator.com/ws/list";
    private final String CHOICE_URL = "http://api-en4.akinator.com/ws/choice";
    private final String EXCLUSION_URL = "http://api-en4.akinator.com/ws/exclusion";

    public final JDA jda;
    public final String channelId;
    private final String userId;
    private StepInfo stepInfo;
    private final AbstractScopedEventListener listener;

    private final String signature;
    private final String session;
    private Guess guess;
    private boolean lastQuestionWasGuess = false;

    public AkinatorListener(JDA jda, AbstractScopedEventListener listener, String channelId, String userId) throws UnirestException {
        this.jda = jda;
        this.listener = listener;
        this.channelId = channelId;
        this.userId = userId;

        jda.getTextChannelById(channelId).sendTyping();

        //Start new session
        JSONObject json = Unirest.get(NEW_SESSION_URL).asJson().getBody().getObject();
        stepInfo = new StepInfo(json);

        signature = stepInfo.getSignature();
        session = stepInfo.getSession();

        sendNextQuestion();
    }

    private void sendNextQuestion() {
        String name = jda.getTextChannelById(channelId).getGuild().getEffectiveNameForUser(jda.getUserById(userId));
        String out = "**" + name + ": Question " + (stepInfo.getStepNum() + 1) + "**\n"
                + stepInfo.getQuestion() + "\n [yes/no/idk/probably/probably not]";
        jda.getTextChannelById(channelId).sendMessage(out);
        lastQuestionWasGuess = false;
    }

    private void sendGuess() throws UnirestException {
        guess = new Guess();
        String out = "Is this your character?\n" + guess.toString() + "\n[yes/no]";
        jda.getTextChannelById(channelId).sendMessage(out);
        lastQuestionWasGuess = true;
    }

    private void answerQuestion(byte answer) {
        try {
            JSONObject json = Unirest.get(ANSWER_URL)
                    .queryString("session", session)
                    .queryString("signature", signature)
                    .queryString("step", stepInfo.getStepNum())
                    .queryString("answer", answer)
                    .asJson().getBody().getObject();
            stepInfo = new StepInfo(json);

            if (stepInfo.getProgression() > 90) {
                sendGuess();
            } else {
                sendNextQuestion();
            }
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void answerGuess(byte answer) {
        try {
            if (answer == 0) {
                Unirest.get(CHOICE_URL)
                        .queryString("session", session)
                        .queryString("signature", signature)
                        .queryString("step", stepInfo.getStepNum())
                        .queryString("element", guess.getId())
                        .asString();
                jda.getTextChannelById(channelId).sendMessage("Great ! Guessed right one more time.\n"
                        + "I love playing with you!\n"
                        + "<http://akinator.com>");
                FredBoat.getListenerBot().removeListener(userId);
            } else if (answer == 1) {
                Unirest.get(EXCLUSION_URL)
                        .queryString("session", session)
                        .queryString("signature", signature)
                        .queryString("step", stepInfo.getStepNum())
                        .queryString("forward_answer", answer)
                        .asString();

                lastQuestionWasGuess = false;
                sendNextQuestion();
            }
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Channel channel = event.getChannel();

        if (!channel.getId().equals(channelId)) {
            return;
        }

        byte answer;
        //<editor-fold defaultstate="collapsed" desc="switch">
        switch (event.getMessage().getStrippedContent().trim().toLowerCase()) {
            case "yes":
                answer = 0;
                break;
            case "y":
                answer = 0;
                break;
            case "no":
                answer = 1;
                break;
            case "n":
                answer = 1;
                break;
            case "idk":
                answer = 2;
                break;
            case "p":
                answer = 3;
                break;
            case "probably":
                answer = 3;
                break;
            case "pn":
                answer = 4;
                break;
            case "probably not":
                answer = 4;
                break;
            default:
                answer = -1;
                break;
        }
//</editor-fold>

        if (answer == -1) {
            return;
        }

        if (lastQuestionWasGuess) {
            if (answer != 0 && answer != 1) {
                return;
            }

            jda.getTextChannelById(channelId).sendTyping();
            answerGuess(answer);
        } else {
            jda.getTextChannelById(channelId).sendTyping();
            answerQuestion(answer);
        }
    }

    private class StepInfo {

        private String signature = "";
        private String session = "";
        private final String question;
        private final int stepNum;
        private final double progression;

        public StepInfo(JSONObject json) {
            JSONObject params = json.getJSONObject("parameters");
            JSONObject info = params.has("step_information") ? params.getJSONObject("step_information") : params;
            question = info.getString("question");
            stepNum = info.getInt("step");
            progression = info.getDouble("progression");

            JSONObject identification = params.optJSONObject("identification");
            if (identification != null) {
                signature = identification.getString("signature");
                session = identification.getString("session");
            }
        }

        public String getQuestion() {
            return question;
        }

        public int getStepNum() {
            return stepNum;
        }

        public String getSignature() {
            return signature;
        }

        public String getSession() {
            return session;
        }

        public double getProgression() {
            return progression;
        }

    }

    private class Guess {

        private final String id;
        private final String name;
        private final String desc;
        private final int ranking;
        private final String pseudo;
        private final String imgPath;

        public Guess() throws UnirestException {
            JSONObject json = Unirest.get(GET_GUESS_URL)
                    .queryString("session", session)
                    .queryString("signature", signature)
                    .queryString("step", stepInfo.getStepNum())
                    .asJson()
                    .getBody()
                    .getObject();

            JSONObject character = json.getJSONObject("parameters")
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("element");

            id = character.getString("id");
            name = character.getString("name");
            desc = character.getString("description");
            ranking = character.getInt("ranking");
            pseudo = character.getString("pseudo");
            imgPath = character.getString("absolute_picture_path");
        }

        public String getDesc() {
            return desc;
        }

        public String getImgPath() {
            return imgPath;
        }

        public String getName() {
            return name;
        }

        public String getPseudo() {
            return pseudo;
        }

        public int getRanking() {
            return ranking;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            String str = "**" + name + "**\n"
                    //+ (!pseudo.equals("none") ? "(" + pseudo + ")\n" : "")
                    + desc + "\n"
                    + "Ranking as **#" + ranking + "**\n"
                    + imgPath;
            return str;
        }

    }

}
