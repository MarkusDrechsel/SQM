package at.ac.tuwien.inso.sqm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.Tag;
import at.ac.tuwien.inso.sqm.repository.TagRepository;

@Service
public class TagServiceImpl implements TgaService {

	private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

	
    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<Tag> findAll() {
    	log.info("finding all tags");
        return tagRepository.findAll().stream().filter(Tag::getValid).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAllValid() {
        log.info("finding all tags");
        return tagRepository.findByValidTrue();
    }

    @Override
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

}
