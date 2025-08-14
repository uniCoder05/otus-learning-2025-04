package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.controller.ClientController;
import ru.otus.crm.dto.ClientDTO;

// @SuppressWarnings({"java:S1989"})
public class ClientsApiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ClientsApiServlet.class);

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient ClientController clientController;
    private final transient Gson gson;

    public ClientsApiServlet(ClientController clientController, Gson gson) {
        this.clientController = clientController;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        log.info("ServletPath: {}, PathInfo: {}", servletPath, pathInfo);

        if ("/api/clients".equals(servletPath) && pathInfo == null) {
            List<ClientDTO> result = clientController.getAllClients();
            out.print(gson.toJson(result));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(Map.of("Ошибка", "Ресурс не найден")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        ServletOutputStream out = response.getOutputStream();
        try {
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info("Отправлен POST запрос: {}", requestBody);
            ClientDTO newClientDTO = gson.fromJson(requestBody, ClientDTO.class);
            ClientDTO savedClientDTO = clientController.create(newClientDTO);
            log.info("Клиент сохранён: {}", savedClientDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(savedClientDTO));
        } catch (Exception e) {
            log.error("Ошибка при создании клиента", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("Ошибка", "Не получилось создать клиента", "message", e.getMessage())));
        }
    }

    private long extractIdFromRequest(String pathInfo) {
        String[] path = pathInfo.split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}
