
const SBOLDocument = require('sboljs')
const fs = require('fs')

const sevaNS = 'http://wiki.synbiohub.org/wiki/Terms/seva#'

loadRDF('parts.xml').then((partsDoc) => {

    //const [ partsDoc, pSevaDoc ] = res

    console.log('loaded xml')


    const cargos = partsDoc.componentDefinitions.filter((componentDefinition) => {
        return componentDefinition.getAnnotation(sevaNS + 'cargoNumber') || false
    })

    const antibioticResistance = partsDoc.componentDefinitions.filter((componentDefinition) => {
        return componentDefinition.getAnnotation(sevaNS + 'antibioticResistanceNumber') || false
    })

    const originsOfReplication = partsDoc.componentDefinitions.filter((componentDefinition) => {
        return componentDefinition.getAnnotation(sevaNS + 'originOfReplicationNumber') || false
    })

    console.log('Cargos:', cargos.map((cd) => cd.name))
    console.log('Antibiotic resistance:', antibioticResistance.map((cd) => cd.name))
    console.log('Origins of replication:', originsOfReplication.map((cd) => cd.name))

    antibioticResistance.forEach((antibioticResistance) => {
        originsOfReplication.forEach((originOfReplication) => {
            cargos.forEach((cargo) => {
                createVariant(antibioticResistance, originOfReplication, cargo)
            })
        })
    })

    function createVariant(antibioticResistance, originOfReplication, cargo) {

        const antibioticResistanceNumber = parseInt(antibioticResistance.getAnnotation(sevaNS + 'antibioticResistanceNumber'))
        const originOfReplicationNumber = parseInt(originOfReplication.getAnnotation(sevaNS + 'originOfReplicationNumber'))
        const cargoNumber = parseInt(cargo.getAnnotation(sevaNS + 'cargoNumber'))

        const plasmidID = 'pSEVA' + antibioticResistanceNumber + originOfReplicationNumber + cargoNumber

        console.log('Creating variant: ' + plasmidID)
        console.log('   Antibiotic Resistance: ' + antibioticResistance.name)
        console.log('   Origin of Replication: ' + originOfReplication.name)
        console.log('   Cargo: ' + cargo.name)


        const sbol = new SBOLDocument()

        const variant = sbol.componentDefinition()
        variant.displayId = plasmidID
        variant.version = '1'
        variant.name = plasmidID
        variant.description = 'SEVA plasmid variant with ' + antibioticResistance.name + ' resistance, ' + originOfReplication.name + ' origin of replication, and ' + cargo.name + ' cargo'
        variant.persistentIdentity = 'http://synbiohub.org/public/seva/' + variant.displayId
        variant.uri = variant.persistentIdentity + '/' + variant.version

        const pSevaComponent = sbol.component()
        pSevaComponent.displayId = 'pSevaTemplate'
        pSevaComponent.version = '1'
        pSevaComponent.persistentIdentity = variant.persistentIdentity + '/' + pSevaComponent.displayId
        pSevaComponent.uri = pSevaComponent.persistentIdentity + '/' + pSevaComponent.version
        pSevaComponent.definition = 'http://synbiohub.org/public/seva/pSevaTemplate/1'




        const antibioticResistanceComponent = sbol.component()
        antibioticResistanceComponent.displayId = antibioticResistance.displayId
        antibioticResistanceComponent.version = '1'
        antibioticResistanceComponent.persistentIdentity = variant.persistentIdentity + '/' + antibioticResistanceComponent.displayId
        antibioticResistanceComponent.uri = antibioticResistanceComponent.persistentIdentity + '/' + antibioticResistanceComponent.version
        antibioticResistanceComponent.definition = antibioticResistance.uri

        const originOfReplicationComponent = sbol.component()
        originOfReplicationComponent.displayId = originOfReplication.displayId
        originOfReplicationComponent.version = '1'
        originOfReplicationComponent.persistentIdentity = variant.persistentIdentity + '/' + originOfReplicationComponent.displayId
        originOfReplicationComponent.uri = originOfReplicationComponent.persistentIdentity + '/' + originOfReplicationComponent.version
        originOfReplicationComponent.definition = originOfReplication.uri

        const cargoComponent = sbol.component()
        cargoComponent.displayId = cargo.displayId
        cargoComponent.version = '1'
        cargoComponent.persistentIdentity = variant.persistentIdentity + '/' + cargoComponent.displayId
        cargoComponent.uri = cargoComponent.persistentIdentity + '/' + cargoComponent.version
        cargoComponent.definition = cargo.uri




        variant.addComponent(pSevaComponent)
        variant.addComponent(antibioticResistanceComponent)
        variant.addComponent(originOfReplicationComponent)
        variant.addComponent(cargoComponent)




        const antibioticResistanceMapping = sbol.mapping()
        antibioticResistanceMapping.displayId = 'antibioticResistanceMapping'
        antibioticResistanceMapping.version = '1'
        antibioticResistanceMapping.persistentIdentity = pSevaComponent.persistentIdentity + '/' + antibioticResistanceMapping.displayId
        antibioticResistanceMapping.uri = antibioticResistanceMapping.persistentIdentity + '/' + antibioticResistanceMapping.version
        antibioticResistanceMapping.local = antibioticResistanceComponent.uri
        antibioticResistanceMapping.remote = 'http://synbiohub.org/public/seva/pSevaTemplate/antibiotic_resistance/1'
        antibioticResistanceMapping.refinement = 'http://sbols.org/v2#useLocal'

        const originOfReplicationMapping = sbol.mapping()
        originOfReplicationMapping.displayId = 'originOfReplicationMapping'
        originOfReplicationMapping.version = '1'
        originOfReplicationMapping.persistentIdentity = pSevaComponent.persistentIdentity + '/' + originOfReplicationMapping.displayId
        originOfReplicationMapping.uri = originOfReplicationMapping.persistentIdentity + '/' + originOfReplicationMapping.version
        originOfReplicationMapping.local = originOfReplicationComponent.uri
        originOfReplicationMapping.remote = 'http://synbiohub.org/public/seva/pSevaTemplate/oriR/1'
        originOfReplicationMapping.refinement = 'http://sbols.org/v2#useLocal'
        
        const cargoMapping = sbol.mapping()
        cargoMapping.displayId = 'cargoMapping'
        cargoMapping.version = '1'
        cargoMapping.persistentIdentity = pSevaComponent.persistentIdentity + '/' + cargoMapping.displayId
        cargoMapping.uri = cargoMapping.persistentIdentity + '/' + cargoMapping.version
        cargoMapping.local = cargoComponent.uri
        cargoMapping.remote = 'http://synbiohub.org/public/seva/pSevaTemplate/cargo/1'
        cargoMapping.refinement = 'http://sbols.org/v2#useLocal'


        pSevaComponent.addMapping(antibioticResistanceMapping)
        pSevaComponent.addMapping(originOfReplicationMapping)
        pSevaComponent.addMapping(cargoMapping)


        fs.writeFileSync('./out/' + plasmidID + '.xml', sbol.serializeXML())
    }
})


function loadRDF(filename) {

    return new Promise((resolve, reject) => {

        SBOLDocument.loadRDFFile(filename, (err, sbol) => {

            if(err) {
                reject(err)
            } else {
                resolve(sbol)
            }

        })

    })

}

