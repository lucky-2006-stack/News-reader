package com.example.data.remote

import com.example.data.model.Article
import com.example.data.model.NewsCategory

object CuratedNewsData {

    val breakingNews: List<Article> = listOf(
        Article(
            id = "breaking_quantum_leap_2026",
            title = "Major Breakthrough in Quantum Computing Achieves Room-Temperature Coherence",
            description = "Researchers at an international consortium have sustained quantum state coherence at standard ambient temperatures for over ten minutes.",
            content = """
                In what is being hailed as one of the most significant physics milestones of the decade, an international research team has achieved stable quantum qubit coherence at ambient room temperature for more than ten consecutive minutes.
                
                Previously, quantum computers required massive cryogenic dilution refrigerators cooled to near absolute zero (-273.15°C) to prevent thermal noise from destroying fragile quantum superpositions. The new technique utilizes diamond nitrogen-vacancy centers paired with novel photonic topological lattices, isolating quantum states from atmospheric molecular vibrations.
                
                "This transitions quantum systems from room-filling laboratory wonders into modular, practical architectures," noted Dr. Elena Vance, lead investigator on the project. "It fundamentally changes how we envision practical drug discovery, molecular synthesis modeling, and cryptographic resilience."
                
                Commercial viability is estimated within three to five years, with cloud computing providers already racing to design room-temperature modular quantum server racks.
            """.trimIndent(),
            author = "Dr. Elena Vance",
            source = "Pulse Science Desk",
            sourceUrl = "https://example.com/quantum-leap",
            imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.TECH,
            publishedAt = "15m ago",
            readTimeMinutes = 4
        ),
        Article(
            id = "breaking_clean_energy_grid",
            title = "Global Clean Energy Generation Crosses Historic 45% Milestone",
            description = "Driven by record solar deployment and next-gen geothermal plants, low-carbon generation hits an all-time global benchmark.",
            content = """
                Global energy monitors confirmed today that combined renewable energy production—encompassing ultra-efficient photovoltaic installations, offshore wind arrays, and next-generation closed-loop geothermal—has surpassed 45% of worldwide electricity generation.
                
                Energy analysts cite rapid grid-scale battery storage installations over the past eighteen months, which have resolved intermittency bottlenecks in major industrial corridors across North America, Europe, and Asia.
                
                "We are witnessing an unprecedented decoupling of economic expansion from carbon intensity," stated the Global Energy Transition Council. Grid stability has actually improved in regions adopting AI-driven automated demand-response balancing systems.
            """.trimIndent(),
            author = "Marcus Thorne",
            source = "Global Energy Review",
            sourceUrl = "https://example.com/clean-energy",
            imageUrl = "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.BUSINESS,
            publishedAt = "32m ago",
            readTimeMinutes = 3
        )
    )

    val allArticles: List<Article> = breakingNews + listOf(
        Article(
            id = "tech_neuromorphic_chips",
            title = "Next-Generation Neuromorphic Chips Slash AI Data Center Power Usage by 80%",
            description = "Brain-inspired analog processing architectures promise to make exponential AI model training sustainable and edge-friendly.",
            content = """
                Silicon innovators have demonstrated a new generation of neuromorphic processors that emulate biological neural synapses through analog memristive arrays.
                
                Traditional digital GPUs consume enormous quantities of electrical power by shuttling vast matrices between memory registers and compute cores. Neuromorphic compute-in-memory architectures eliminate the classic von Neumann bottleneck, performing matrix-vector arithmetic directly within the physical storage layer.
                
                Benchmark tests run against state-of-the-art transformer models indicated a reduction in power consumption of over 80%, while maintaining 99.4% inference accuracy. Server infrastructure operators project billions in energy savings and substantial reductions in cooling water usage.
            """.trimIndent(),
            author = "Sophia Chen",
            source = "Tech Horizon",
            sourceUrl = "https://example.com/neuromorphic",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.TECH,
            publishedAt = "1h ago",
            readTimeMinutes = 5
        ),
        Article(
            id = "business_fintech_sovereign_rails",
            title = "Central Banks Expand 24/7 Cross-Border Instant Payment Interoperability",
            description = "A multilateral initiative links domestic instant payment networks across fourteen economies, slashing international remittance fees to near zero.",
            content = """
                Sending funds across international borders is undergoing its most radical transformation since the inception of the SWIFT messaging protocol. Today, central bank governors announced the operational launch of Project Nexus 2.0.
                
                The system directly bridges domestic real-time gross settlement (RTGS) rails, enabling instant peer-to-peer and merchant payments across borders in under ten seconds at transparent, wholesale foreign exchange rates.
                
                Retail banks and independent digital wallets are rolling out support immediately, providing millions of migrant workers and small-to-medium enterprises with frictionless international trade settlements without intermediary correspondent banking fees.
            """.trimIndent(),
            author = "Julian Rossi",
            source = "Financial Chronicle",
            sourceUrl = "https://example.com/payments",
            imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.BUSINESS,
            publishedAt = "2h ago",
            readTimeMinutes = 4
        ),
        Article(
            id = "world_deep_sea_sanctuary",
            title = "Historic Treaty Designates 2.5 Million Square Kilometers of High Seas as Protected Sanctuary",
            description = "Envoys from over 120 nations finalize the ratification of landmark ocean conservation boundaries in the South Pacific.",
            content = """
                Following marathon negotiations at the United Nations headquarters, international delegates have formally signed the ratification documents establishing the largest contiguous marine sanctuary on Earth.
                
                Spanning over 2.5 million square kilometers of the South Pacific abyssal plain and seamount chains, the zone is now legally safeguarded against deep-sea polymetallic nodule mining, industrial bottom trawling, and unmonitored acoustic surveying.
                
                Marine biologists celebrated the accord as a pivotal defense line for undiscovered biodiversity, carbon-sequestration benthic organisms, and migratory pelagic corridors. Satellite monitoring and autonomous maritime drones will patrol the boundary in real time.
            """.trimIndent(),
            author = "Amara Diallo",
            source = "World Dispatch",
            sourceUrl = "https://example.com/deep-sea",
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.WORLD,
            publishedAt = "3h ago",
            readTimeMinutes = 5
        ),
        Article(
            id = "science_mars_subsurface_aquifer",
            title = "Radar Sounding Confirms Vast Liquid Water Reservoir Beneath Martian South Pole",
            description = "New synthetic aperture radar scans indicate active geothermal gradients maintaining pressurized liquid brine lakes deep underground.",
            content = """
                Planetary scientists analyzing ultra-high-resolution subsurface radar echoes from the ExoMars Trace Gas Orbiter have confirmed definitive signatures of liquid water reservoirs beneath the south polar ice cap of Mars.
                
                The data shows layered dielectric properties that can only be accounted for by liquid, mineral-dense hypersaline brines trapped four kilometers beneath the frozen surface. Heat flow models suggest localized magmatic activity may still warm pockets of the Martian mantle.
                
                The revelation drastically recalculates the probabilities for subterranean extremophile life and provides tantalizing potential resources for planned human exploration expeditions in the coming decades.
            """.trimIndent(),
            author = "Dr. Neil Sterling",
            source = "AstroPhysical Annals",
            sourceUrl = "https://example.com/mars-water",
            imageUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.SCIENCE,
            publishedAt = "4h ago",
            readTimeMinutes = 4
        ),
        Article(
            id = "culture_heritage_virtual_reconstruction",
            title = "Lost Architectural Wonders Rebuilt in Photorealistic Spatial Archival",
            description = "Historians and 3D LiDAR specialists recreate destroyed ancient wonders with millimeter-precise digital restoration.",
            content = """
                Centuries-old architectural treasures that were lost to conflict, earthquakes, or urban redevelopment are receiving second lives through an open-access cultural conservation initiative.
                
                Using a combination of archival photographs, historical architectural blueprints, drone photogrammetry, and generative volumetric rendering, researchers have digitally reconstructed ancient temples, forgotten city squares, and historical landmarks.
                
                Visitors worldwide can now step inside these historical spaces via open-web interactive spatial viewers, while museums offer guided educational walkthroughs for students and researchers across continents.
            """.trimIndent(),
            author = "Clara Valente",
            source = "Arts & Heritage Review",
            sourceUrl = "https://example.com/virtual-heritage",
            imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.CULTURE,
            publishedAt = "5h ago",
            readTimeMinutes = 3
        ),
        Article(
            id = "sports_biomechanics_athletics",
            title = "Smart Fabrics and Micro-Sensors Revolutionize Athletic Performance & Injury Prevention",
            description = "Next-generation strain sensors woven directly into athletic apparel detect micro-fatigue before muscle tears occur.",
            content = """
                Elite sports medicine is experiencing a data revolution as professional football, basketball, and track teams integrate piezoelectric carbon-nanotube threads into standard match jerseys.
                
                The threads continuously capture biomechanical torque, asymmetric load distribution, and joint acceleration at 1,000 samples per second. When kinetic irregularities indicate dangerous muscle exhaustion, coaching staffs receive real-time alerts on sideline tablets.
                
                Early clinical trials across premier leagues report a 42% reduction in non-contact soft-tissue injuries over the course of a competitive season.
            """.trimIndent(),
            author = "Derrick Owens",
            source = "Athletic Science Digest",
            sourceUrl = "https://example.com/smart-athletics",
            imageUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.SPORTS,
            publishedAt = "6h ago",
            readTimeMinutes = 4
        ),
        Article(
            id = "tech_privacy_on_device_agents",
            title = "The Rise of Local Personal AI: Why the World is Moving Back to On-Device Processing",
            description = "High-speed small parameter models and dedicated NPUs are allowing complete privacy without sacrificing intelligent capability.",
            content = """
                For years, the artificial intelligence industry relied entirely on massive hyperscale server clusters. However, a major counter-movement is taking hold as powerful small-language models are designed specifically to run directly on consumer smartphones and laptops.
                
                With zero data leaving the device's encrypted memory, users enjoy intelligent voice dictation, context-aware scheduling, and automated document synthesis with zero cloud latency and absolute personal privacy.
                
                "The era of sending your personal thoughts and private calendar to remote servers is coming to an end," stated personal computing analysts. "Edge intelligence is the new standard of trust."
            """.trimIndent(),
            author = "Kavita Nair",
            source = "Tech Horizon",
            sourceUrl = "https://example.com/local-ai",
            imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1000&auto=format&fit=crop&q=80",
            category = NewsCategory.TECH,
            publishedAt = "7h ago",
            readTimeMinutes = 5
        )
    )
}
