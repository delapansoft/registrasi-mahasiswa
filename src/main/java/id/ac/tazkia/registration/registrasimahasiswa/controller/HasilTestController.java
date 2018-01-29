package id.ac.tazkia.registration.registrasimahasiswa.controller;

import id.ac.tazkia.registration.registrasimahasiswa.dao.GradeDao;
import id.ac.tazkia.registration.registrasimahasiswa.dao.HasilTestDao;
import id.ac.tazkia.registration.registrasimahasiswa.dao.PendaftarDao;
import id.ac.tazkia.registration.registrasimahasiswa.entity.Grade;
import id.ac.tazkia.registration.registrasimahasiswa.entity.HasilTest;
import id.ac.tazkia.registration.registrasimahasiswa.entity.Pendaftar;
import id.ac.tazkia.registration.registrasimahasiswa.service.RegistrasiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
public class HasilTestController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrasiDetailController.class);


    @Autowired
    private PendaftarDao pendaftarDao;
    @Autowired
    private GradeDao gradeDao;
    @Autowired
    private HasilTestDao hasilTestDao;
    @Autowired
    private RegistrasiService registrasiService;


    @ModelAttribute("daftarGrade")
    public Iterable<Grade> daftarGrade(){return gradeDao.findAll(); }

//tampikan form
    @RequestMapping(value = "/registrasi/hasil/form", method = RequestMethod.GET)
    public void tampilkanForm(@RequestParam(value = "id", required = true) String id,
                              @RequestParam(required = false) String error,
                              Model m){
        //defaultnya, isi dengan object baru
        HasilTest h = new HasilTest();
        m.addAttribute("hasil", h);
        m.addAttribute("error", error);

        Pendaftar p = pendaftarDao.findOne(id);
        m.addAttribute("pendaftar", p);
        if (p != null){
            h.setPendaftar(p);
        }

        HasilTest d = hasilTestDao.findByPendaftar(p);
        logger.debug("Nomor Registrasi :"+ p.getNomorRegistrasi());
        if (d != null){
            m.addAttribute("hasil", d);
        }
    }
////

//simpan
    @RequestMapping(value = "registrasi/hasil/form", method = RequestMethod.POST)
    public String prosesForm(@Valid HasilTest hasilTest,String nilai, BindingResult errors){
        if(errors.hasErrors()){
            return "/registrasi/hasil/form";
        }


        Grade hasil = registrasiService.hitungGrade(new BigDecimal(nilai));
        logger.debug("ID GRADE :"+ hasil.getId());

        hasilTest.setGrade(hasil);

        hasilTestDao.save(hasilTest);
        return "redirect:/registrasi/list";
    }

////

//List

    @GetMapping("registrasi/hasil/list")
    public String listHasilTest(@RequestParam(required = false)String search, Model m, Pageable page){
        if(StringUtils.hasText(search)) {
            m.addAttribute("search", search);
            m.addAttribute("daftarHasil", hasilTestDao.findByPendaftarNomorRegistrasiContainingOrPendaftarNamaOrGradeNamaContainingIgnoreCaseOrderByPendaftarNomorRegistrasi(search,search, search, page));
        } else {
            m.addAttribute("daftarHasil", hasilTestDao.findAll(page));
        }
        return "registrasi/hasil/list";
    }
}
