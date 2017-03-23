package br.com.denis.boaviagem.calendar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import br.com.denis.boaviagem.domain.Viagem;
import br.com.denis.boaviagem.helper.Constantes;

public class CalendarService {

    public CalendarService(String nomeConta, String tokenAcesso) {
        this.nomeConta = nomeConta;
        GoogleCredential credencial = new GoogleCredential();
        credencial.setAccessToken(tokenAcesso);
        HttpTransport transport =
                AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = new GsonFactory();
        calendar = new Calendar.Builder(transport, jsonFactory, credencial)
                .setApplicationName(Constantes.APP_NAME)
                .setHttpRequestInitializer(credencial)
                .build();
    }

    private Calendar calendar;
    private String nomeConta;

    public String criarEvento(Viagem viagem) {
        Event evento = new Event();
        evento.setSummary(viagem.getDestino());
        List<EventAttendee> participantes =
                Arrays.asList((
                        new EventAttendee().setEmail(nomeConta)));
        evento.setAttendees(participantes);
        DateTime inicio = new DateTime(viagem.getDataChegada(),
                TimeZone.getDefault());
        DateTime fim = new DateTime(viagem.getDataSaida(),
                TimeZone.getDefault());
        evento.setStart(new EventDateTime().setDateTime(inicio));
        evento.setEnd(new EventDateTime().setDateTime(fim));
        try {
            Event eventoCriado = calendar.events()
                    .insert(nomeConta, evento)
                    .execute();
            return eventoCriado.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
